package tacit.library

class LibraryConfigSuite extends munit.FunSuite:

  // ── fromJson ────────────────────────────────────────────────

  test("empty string returns defaults"):
    val cfg = LibraryConfig.fromJson("")
    assertEquals(cfg.strictMode, None)
    assertEquals(cfg.classifiedPaths, None)
    assertEquals(cfg.llm, None)

  test("empty object returns defaults"):
    val cfg = LibraryConfig.fromJson("{}")
    assertEquals(cfg.strictMode, None)
    assertEquals(cfg.classifiedPaths, None)
    assertEquals(cfg.llm, None)

  test("parses strictMode"):
    val cfg = LibraryConfig.fromJson("""{"strictMode": false}""")
    assertEquals(cfg.strictMode, Some(false))

  test("parses classifiedPaths"):
    val cfg = LibraryConfig.fromJson("""{"classifiedPaths": ["/a", "/b"]}""")
    assertEquals(cfg.classifiedPaths, Some(Set("/a", "/b")))

  test("parses llm"):
    val cfg = LibraryConfig.fromJson("""{
      "llm": { "baseUrl": "https://api.example.com", "apiKey": "sk-test", "model": "m1" }
    }""")
    assert(cfg.llm.isDefined)
    assertEquals(cfg.llm.get.baseUrl, "https://api.example.com")
    assertEquals(cfg.llm.get.apiKey, "sk-test")
    assertEquals(cfg.llm.get.model, "m1")

  test("parses full config"):
    val cfg = LibraryConfig.fromJson("""{
      "strictMode": true,
      "classifiedPaths": ["/secret"],
      "llm": { "baseUrl": "https://example.com", "apiKey": "key", "model": "gpt" }
    }""")
    assertEquals(cfg.strictMode, Some(true))
    assertEquals(cfg.classifiedPaths, Some(Set("/secret")))
    assert(cfg.llm.isDefined)

  test("missing fields are None, not errors"):
    val cfg = LibraryConfig.fromJson("""{"strictMode": true}""")
    assertEquals(cfg.strictMode, Some(true))
    assertEquals(cfg.classifiedPaths, None)
    assertEquals(cfg.llm, None)

  test("invalid JSON throws"):
    intercept[RuntimeException] {
      LibraryConfig.fromJson("not json")
    }

  test("wrong field type throws"):
    intercept[RuntimeException] {
      LibraryConfig.fromJson("""{"strictMode": "yes"}""")
    }

  test("incomplete llm throws"):
    intercept[RuntimeException] {
      LibraryConfig.fromJson("""{"llm": {"baseUrl": "x"}}""")
    }

  test("ignores unknown fields"):
    val cfg = LibraryConfig.fromJson("""{"strictMode": true, "unknownField": 42}""")
    assertEquals(cfg.strictMode, Some(true))

  test("parses secureOutput"):
    val cfg = LibraryConfig.fromJson("""{"secureOutput": "/tmp/tacit-secure.log"}""")
    assertEquals(cfg.secureOutput, Some("/tmp/tacit-secure.log"))

  test("secureOutput defaults to None"):
    val cfg = LibraryConfig.fromJson("""{"strictMode": true}""")
    assertEquals(cfg.secureOutput, None)

  // ── LlmConfig toString hides API key ────────────────────────

  test("LlmConfig toString hides apiKey"):
    val llm = LlmConfig("https://api.example.com", "secret-key-123", "gpt-4")
    assert(!llm.toString.contains("secret-key-123"))
    assert(llm.toString.contains("***"))
