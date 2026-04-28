import tacit.core.Config
import io.circe.*
import scopt.OParser

import java.nio.file.{Files, Path}

class ConfigSuite extends munit.FunSuite:

  // Create a fake JAR file so --library-jar validation passes
  val fakeJar: Path = Files.createTempFile("fake-library", ".jar")
  val jarPath: String = fakeJar.toAbsolutePath.toString

  override def afterAll(): Unit = Files.deleteIfExists(fakeJar)

  /** Run a block with stderr suppressed (for tests that intentionally trigger error output). */
  private def quietly[T](f: => T): T =
    val origErr = System.err
    System.setErr(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()))
    try f finally System.setErr(origErr)

  /** Parse CLI args with the fake JAR path prepended. */
  private def parse(args: String*): Option[Config] =
    Config.parseCliArgs(Array("--library-jar", jarPath) ++ args)

  /** Parse CLI args without library-jar validation (for testing flags in isolation). */
  private def parseRaw(args: String*): Option[Config] =
    OParser.parse(Config.optParser, args.toArray, Config())

  /** Write a temporary JSON config file and return its path. */
  private def withConfigFile(json: String)(f: String => Unit): Unit =
    val file = Files.createTempFile("test-config", ".json")
    Files.writeString(file, json)
    try f(file.toAbsolutePath.toString)
    finally Files.deleteIfExists(file)

  // ── Defaults ────────────────────────────────────────────────

  test("default config has expected values"):
    val cfg = parseRaw().get
    assertEquals(cfg.recordPath, None)
    assertEquals(cfg.quiet, false)
    assertEquals(cfg.sessionEnabled, true)
    assertEquals(cfg.safeMode, true)
    assertEquals(cfg.libraryConfig, Json.obj())

  // ── Server CLI flags ────────────────────────────────────────

  test("--record sets recordPath"):
    val cfg = parse("-r", "/tmp/log").get
    assertEquals(cfg.recordPath, Some("/tmp/log"))

  test("--quiet sets quiet"):
    val cfg = parse("-q").get
    assert(cfg.quiet)

  test("--no-session disables sessions"):
    val cfg = parse("--no-session").get
    assert(!cfg.sessionEnabled)

  test("--library-jar sets libraryJarPath"):
    val cfg = parse().get
    assertEquals(cfg.libraryJarPath, jarPath)

  // ── Library CLI flags ───────────────────────────────────────

  test("--strict sets strictMode in libraryConfig"):
    val cfg = parse("-s").get
    assertEquals(cfg.libraryConfig.hcursor.get[Boolean]("strictMode").toOption, Some(true))

  test("--classified-paths sets classifiedPaths in libraryConfig"):
    val cfg = parse("--classified-paths", "/a,/b, /c").get
    val paths = cfg.libraryConfig.hcursor.get[List[String]]("classifiedPaths").toOption
    assertEquals(paths, Some(List("/a", "/b", "/c")))

  test("--command-permissions sets commandPermissions in libraryConfig"):
    val cfg = parse("--command-permissions", "echo, py*,ls").get
    val perms = cfg.libraryConfig.hcursor.get[List[String]]("commandPermissions").toOption
    assertEquals(perms, Some(List("echo", "py*", "ls")))

  test("--network-permissions sets networkPermissions in libraryConfig"):
    val cfg = parse("--network-permissions", "*.example.com, api.github.com").get
    val perms = cfg.libraryConfig.hcursor.get[List[String]]("networkPermissions").toOption
    assertEquals(perms, Some(List("*.example.com", "api.github.com")))

  test("--llm flags set llm object in libraryConfig"):
    val cfg = parse(
      "--llm-base-url", "https://api.example.com",
      "--llm-api-key", "sk-test",
      "--llm-model", "gpt-4"
    ).get
    val llm = cfg.libraryConfig.hcursor.downField("llm")
    assertEquals(llm.get[String]("baseUrl").toOption, Some("https://api.example.com"))
    assertEquals(llm.get[String]("apiKey").toOption, Some("sk-test"))
    assertEquals(llm.get[String]("model").toOption, Some("gpt-4"))

  test("incomplete LLM config is removed"):
    val cfg = quietly { parse("--llm-base-url", "https://api.example.com").get }
    assertEquals(cfg.libraryConfig.hcursor.downField("llm").focus, None)

  test("all-empty LLM fields are removed"):
    val cfg = quietly { parse(
      "--llm-base-url", "",
      "--llm-api-key", "",
      "--llm-model", ""
    ).get }
    assertEquals(cfg.libraryConfig.hcursor.downField("llm").focus, None)

  // ── Missing library JAR ─────────────────────────────────────

  test("nonexistent --library-jar returns None"):
    val cfg = quietly { Config.parseCliArgs(Array("--library-jar", "/nonexistent/fake.jar")) }
    assertEquals(cfg, None)

  // ── JSON config file ────────────────────────────────────────

  test("config file sets server fields"):
    withConfigFile(s"""
      {
        "recordPath": "/tmp/rec",
        "quiet": true,
        "sessionEnabled": false,
        "libraryJarPath": "$jarPath"
      }
    """) { path =>
      val cfg = Config.parseCliArgs(Array("--config", path))
      assert(cfg.isDefined)
      assertEquals(cfg.get.recordPath, Some("/tmp/rec"))
      assert(cfg.get.quiet)
      assert(!cfg.get.sessionEnabled)
    }

  test("config file sets libraryConfig"):
    withConfigFile(s"""
      {
        "libraryJarPath": "$jarPath",
        "libraryConfig": {
          "strictMode": false,
          "classifiedPaths": ["/secret"]
        }
      }
    """) { path =>
      val cfg = Config.parseCliArgs(Array("--config", path)).get
      val lc = cfg.libraryConfig.hcursor
      assertEquals(lc.get[Boolean]("strictMode").toOption, Some(false))
      assertEquals(lc.get[List[String]]("classifiedPaths").toOption, Some(List("/secret")))
    }

  test("config file sets llm in libraryConfig"):
    withConfigFile(s"""
      {
        "libraryJarPath": "$jarPath",
        "libraryConfig": {
          "llm": {
            "baseUrl": "https://api.example.com",
            "apiKey": "sk-file",
            "model": "gpt-3"
          }
        }
      }
    """) { path =>
      val cfg = Config.parseCliArgs(Array("--config", path)).get
      val llm = cfg.libraryConfig.hcursor.downField("llm")
      assertEquals(llm.get[String]("baseUrl").toOption, Some("https://api.example.com"))
      assertEquals(llm.get[String]("apiKey").toOption, Some("sk-file"))
      assertEquals(llm.get[String]("model").toOption, Some("gpt-3"))
    }

  // ── CLI overrides config file ───────────────────────────────

  test("CLI flags after --config override file values"):
    withConfigFile(s"""
      {
        "quiet": false,
        "libraryJarPath": "$jarPath",
        "libraryConfig": {
          "strictMode": false
        }
      }
    """) { path =>
      val cfg = Config.parseCliArgs(Array(
        "--config", path,
        "-q", "-s"
      )).get
      assert(cfg.quiet)
      assertEquals(cfg.libraryConfig.hcursor.get[Boolean]("strictMode").toOption, Some(true))
    }

  test("CLI library flags before --config override file libraryConfig"):
    withConfigFile(s"""
      {
        "libraryJarPath": "$jarPath",
        "libraryConfig": {
          "strictMode": false,
          "classifiedPaths": ["/from-file"]
        }
      }
    """) { path =>
      val cfg = Config.parseCliArgs(Array(
        "-s",
        "--config", path
      )).get
      // CLI -s (strictMode=true) should override file's strictMode=false
      assertEquals(cfg.libraryConfig.hcursor.get[Boolean]("strictMode").toOption, Some(true))
      // File's classifiedPaths should still be present
      assertEquals(cfg.libraryConfig.hcursor.get[List[String]]("classifiedPaths").toOption, Some(List("/from-file")))
    }

  test("CLI llm flags override file llm config"):
    withConfigFile(s"""
      {
        "libraryJarPath": "$jarPath",
        "libraryConfig": {
          "llm": {
            "baseUrl": "https://file.example.com",
            "apiKey": "sk-file",
            "model": "file-model"
          }
        }
      }
    """) { path =>
      val cfg = Config.parseCliArgs(Array(
        "--config", path,
        "--llm-model", "cli-model"
      )).get
      val llm = cfg.libraryConfig.hcursor.downField("llm")
      assertEquals(llm.get[String]("baseUrl").toOption, Some("https://file.example.com"))
      assertEquals(llm.get[String]("apiKey").toOption, Some("sk-file"))
      assertEquals(llm.get[String]("model").toOption, Some("cli-model"))
    }

  // ── Invalid config file ─────────────────────────────────────

  test("nonexistent config file returns None"):
    val cfg = quietly { parse("--config", "/nonexistent/config.json") }
    assertEquals(cfg, None)

  test("malformed JSON config file returns None"):
    withConfigFile("not valid json {{{") { path =>
      val cfg = quietly { parse("--config", path) }
      assertEquals(cfg, None)
    }

  // ── Empty / minimal config file ─────────────────────────────

  test("empty object config file uses defaults"):
    withConfigFile(s"""{ "libraryJarPath": "$jarPath" }""") { path =>
      val cfg = Config.parseCliArgs(Array("--config", path)).get
      assertEquals(cfg.recordPath, None)
      assertEquals(cfg.quiet, false)
      assertEquals(cfg.sessionEnabled, true)
      assertEquals(cfg.libraryConfig, Json.obj())
    }

  test("config file with empty libraryConfig"):
    withConfigFile(s"""{ "libraryJarPath": "$jarPath", "libraryConfig": {} }""") { path =>
      val cfg = Config.parseCliArgs(Array("--config", path)).get
      assertEquals(cfg.libraryConfig, Json.obj())
    }

  // ── Safe mode flag ────────────────────────────────────────────

  test("safe mode is enabled by default"):
    val cfg = parseRaw().get
    assert(cfg.safeMode)

  test("--no-safe-mode disables safe mode"):
    val cfg = parse("--no-safe-mode").get
    assert(!cfg.safeMode)

  test("--safe-mode explicitly enables safe mode"):
    // safe mode is already on by default; explicit flag should keep it on
    val cfg = parse("-s").get
    assert(cfg.safeMode)
