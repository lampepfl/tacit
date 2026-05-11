package tacit.library

import language.experimental.captureChecking
import caps.unsafe.unsafeAssumePure

import java.nio.file.Path

class ClassifiedSuite extends munit.FunSuite:
  // ── Classified[T] unit tests ──────────────────────────────────────────

  test("Classified.apply creates a classified value") {
    val c = ClassifiedImpl.wrap("secret")
    assert(c != null)
  }

  test("Classified.toString hides content") {
    val c = ClassifiedImpl.wrap("secret-password")
    assertEquals(c.toString, "Classified(***)")
  }

  test("Classified.map transforms with pure function") {
    val c = ClassifiedImpl.wrap("hello")
    val upper = c.map(_.toUpperCase)
    assertEquals(upper.toString, "Classified(***)")
  }

  test("Classified.flatMap chains classified operations") {
    val c = ClassifiedImpl.wrap(42)
    val result = c.flatMap(x => ClassifiedImpl.wrap(x * 2))
    assertEquals(result.toString, "Classified(***)")
  }

  test("Classified.map exception does not leak classified value") {
    val secret = ClassifiedImpl.wrap("super-secret-password")
    val result = secret.map { s =>
      throw RuntimeException(s"leaked: $s")
    }
    // The exception is captured inside the Classified — no leak
    assertEquals(result.toString, "Classified(***)")
    // Unwrapping reveals a Failure, but the original exception message stays inside Try
    val tried = ClassifiedImpl.unwrap(result)
    assert(tried.isFailure)
  }

  test("Classified.flatMap exception does not leak classified value") {
    val secret = ClassifiedImpl.wrap("super-secret-password")
    val result = secret.flatMap { s =>
      throw RuntimeException(s"leaked: $s")
    }
    assertEquals(result.toString, "Classified(***)")
    val tried = ClassifiedImpl.unwrap(result)
    assert(tried.isFailure)
  }

  test("Classified.map on failed value short-circuits without executing op") {
    val secret = ClassifiedImpl.wrap("secret")
    val failed = secret.map { s => throw RuntimeException("boom") }
    var executed = false
    val result = failed.map { _ => executed = true; "should not run" }
    assert(!executed)
    assert(ClassifiedImpl.unwrap(result).isFailure)
  }

  // ── File system classified path enforcement (VirtualFileSystem) ───────

  val interface: Interface^{} = new InterfaceImpl(
    LibraryConfig(strictMode = Some(false), classifiedPaths = Some(Set("secret")))
  ) {
    def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
      new VirtualFileSystem(root, filter, classifiedPatterns = classifiedPatterns)
  }.unsafeAssumePure

  import interface.*

  given (IOCapability^{}) = iocap.unsafeAssumePure

  test("isClassified returns true for classified file") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/secret/data.txt")
      assert(file.isClassified)
    }
  }

  test("isClassified returns false for non-classified file") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/public.txt")
      assert(!file.isClassified)
    }
  }

  test("read() on classified file throws SecurityException") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/secret/data.txt")
      val ex = intercept[SecurityException] { file.read() }
      assert(ex.getMessage.nn.contains("classified"))
    }
  }

  test("write() on classified file throws SecurityException") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/secret/data.txt")
      val ex = intercept[SecurityException] { file.write("nope") }
      assert(ex.getMessage.nn.contains("classified"))
    }
  }

  test("readBytes() on classified file throws SecurityException") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/secret/data.txt")
      val ex = intercept[SecurityException] { file.readBytes() }
      assert(ex.getMessage.nn.contains("classified"))
    }
  }

  test("readLines() on classified file throws SecurityException") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/secret/data.txt")
      val ex = intercept[SecurityException] { file.readLines() }
      assert(ex.getMessage.nn.contains("classified"))
    }
  }

  test("append() on classified file throws SecurityException") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/secret/data.txt")
      val ex = intercept[SecurityException] { file.append("nope") }
      assert(ex.getMessage.nn.contains("classified"))
    }
  }

  test("delete() on classified file throws SecurityException") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/secret/data.txt")
      val ex = intercept[SecurityException] { file.delete() }
      assert(ex.getMessage.nn.contains("classified"))
    }
  }

  test("children on classified directory throws SecurityException") {
    requestFileSystem("/virtual") {
      val dir = access("/virtual/secret")
      val ex = intercept[SecurityException] { dir.children }
      assert(ex.getMessage.nn.contains("classified"))
    }
  }

  test("walk() on classified directory throws SecurityException") {
    requestFileSystem("/virtual") {
      val dir = access("/virtual/secret")
      val ex = intercept[SecurityException] { dir.walk() }
      assert(ex.getMessage.nn.contains("classified"))
    }
  }

  test("writeClassified() writes content to classified file") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/secret/data.txt")
      file.writeClassified(classify("top-secret"))
      val content = file.readClassified()
      assertEquals(content.toString, "Classified(***)")
    }
  }

  test("readClassified() on classified file returns Classified[String]") {
    requestFileSystem("/virtual") {
      access("/virtual/secret/data.txt").writeClassified(classify("secret-content"))
      val content = access("/virtual/secret/data.txt").readClassified()
      assertEquals(content.toString, "Classified(***)")
      // Verify content via map
      val upper = content.map(_.toUpperCase)
      assertEquals(upper.toString, "Classified(***)")
    }
  }

  test("readClassified() on non-classified file throws SecurityException") {
    requestFileSystem("/virtual") {
      access("/virtual/public.txt").write("public data")
      val file = access("/virtual/public.txt")
      val ex = intercept[SecurityException] { file.readClassified() }
      assert(ex.getMessage.nn.contains("not classified"))
    }
  }

  test("writeClassified() on non-classified file throws SecurityException") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/public.txt")
      val ex = intercept[SecurityException] { file.writeClassified(classify("data")) }
      assert(ex.getMessage.nn.contains("not classified"))
    }
  }

  test("convenience readClassified/writeClassified on Interface") {
    requestFileSystem("/virtual") {
      writeClassified("/virtual/secret/conv.txt", classify("interface-level"))
      val content = readClassified("/virtual/secret/conv.txt")
      assertEquals(content.toString, "Classified(***)")
    }
  }

  test("round-trip: write classified, read classified, map, write classified") {
    requestFileSystem("/virtual") {
      // Write initial secret
      val secret = classify("original-secret")
      access("/virtual/secret/round.txt").writeClassified(secret)
      // Read it back
      val read1 = access("/virtual/secret/round.txt").readClassified()
      // Transform it
      val transformed = read1.map(s => s"processed: $s")
      // Write transformed version
      access("/virtual/secret/round2.txt").writeClassified(transformed)
      // Read final version
      val read2 = access("/virtual/secret/round2.txt").readClassified()
      assertEquals(read2.toString, "Classified(***)")
      // Verify content through map
      val check = read2.map(_.startsWith("processed:"))
      assertEquals(check.toString, "Classified(***)")
    }
  }

  test("metadata operations work on classified files") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/secret/meta.txt")
      // exists, isDirectory, size, path, name should all work
      assertEquals(file.exists, false)
      assertEquals(file.isDirectory, false)
      assertEquals(file.name, "meta.txt")
      assert(file.path.endsWith("meta.txt"))
      // Write and check size
      file.writeClassified(classify("hello"))
      assertEquals(file.exists, true)
      assertEquals(file.size, 5L)
    }
  }

  // ── Gitignore-style pattern matching tests ──────────────────────────

  private def mkInterface(patterns: Set[String]): Interface^{} =
    new InterfaceImpl(
      LibraryConfig(strictMode = Some(false), classifiedPaths = Some(patterns))
    ) {
      def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
        new VirtualFileSystem(root, filter, classifiedPatterns = classifiedPatterns)
    }.unsafeAssumePure

  test("pattern without slash matches any component") {
    val api = mkInterface(Set(".ssh"))
    import api.*
    given (IOCapability^{}) = iocap.unsafeAssumePure
    requestFileSystem("/virtual") {
      assert(access("/virtual/.ssh").isClassified)
      assert(access("/virtual/home/.ssh/id_rsa").isClassified)
      assert(!access("/virtual/ssh").isClassified)
    }
  }

  test("glob wildcard in component pattern") {
    val api = mkInterface(Set(".env.*"))
    import api.*
    given (IOCapability^{}) = iocap.unsafeAssumePure
    requestFileSystem("/virtual") {
      assert(access("/virtual/.env.local").isClassified)
      assert(access("/virtual/config/.env.production").isClassified)
      assert(!access("/virtual/.env").isClassified)
    }
  }

  test("relative pattern with slash is anchored to root") {
    val api = mkInterface(Set("config/secrets"))
    import api.*
    given (IOCapability^{}) = iocap.unsafeAssumePure
    requestFileSystem("/virtual") {
      assert(access("/virtual/config/secrets").isClassified)
      assert(access("/virtual/config/secrets/key.pem").isClassified)
      assert(!access("/virtual/other/config/secrets").isClassified)
    }
  }

  test("relative pattern with ** matches at any depth") {
    val api = mkInterface(Set("**/secrets"))
    import api.*
    given (IOCapability^{}) = iocap.unsafeAssumePure
    requestFileSystem("/virtual") {
      assert(access("/virtual/secrets").isClassified)
      assert(access("/virtual/a/secrets").isClassified)
      assert(access("/virtual/a/b/secrets/key.txt").isClassified)
      assert(!access("/virtual/not-secrets").isClassified)
    }
  }

  test("relative pattern with wildcard in middle") {
    val api = mkInterface(Set("config/*/keys"))
    import api.*
    given (IOCapability^{}) = iocap.unsafeAssumePure
    requestFileSystem("/virtual") {
      assert(access("/virtual/config/prod/keys").isClassified)
      assert(access("/virtual/config/dev/keys/secret.pem").isClassified)
      assert(!access("/virtual/config/keys").isClassified)
    }
  }

  test("trailing slash is stripped and still matches") {
    val api = mkInterface(Set("secret/"))
    import api.*
    given (IOCapability^{}) = iocap.unsafeAssumePure
    requestFileSystem("/virtual") {
      assert(access("/virtual/secret").isClassified)
      assert(access("/virtual/secret/data.txt").isClassified)
    }
  }

  test("empty pattern matches nothing") {
    val api = mkInterface(Set("", "/"))
    import api.*
    given (IOCapability^{}) = iocap.unsafeAssumePure
    requestFileSystem("/virtual") {
      assert(!access("/virtual/anything").isClassified)
    }
  }
