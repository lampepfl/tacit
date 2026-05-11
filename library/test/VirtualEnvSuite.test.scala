package tacit.library

import language.experimental.captureChecking

import caps.unsafe.unsafeAssumePure

import java.nio.file.{Files, Path}

class VirtualEnvSuite extends munit.FunSuite:

  val interface: Interface^{} = new InterfaceImpl() {
    def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
      new VirtualFileSystem(root, filter, classifiedPatterns = classifiedPatterns)
  }.unsafeAssumePure

  import interface.*

  given (IOCapability^{}) = iocap.unsafeAssumePure

  test("virtual: write and read back") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/new.txt")
      file.write("new content")
      assertEquals(file.read(), "new content")
    }
  }

  test("virtual: append to file") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/log.txt")
      file.write("line1\n")
      file.append("line2\n")
      assertEquals(file.read(), "line1\nline2\n")
    }
  }

  test("virtual: list directory") {
    requestFileSystem("/virtual") {
      access("/virtual/a.txt").write("a")
      access("/virtual/b.txt").write("b")
      val dir = access("/virtual")
      val kids = dir.children
      assertEquals(kids.length, 2)
      assert(kids.exists(_.name == "a.txt"))
      assert(kids.exists(_.name == "b.txt"))
    }
  }

  test("virtual: delete file") {
    requestFileSystem("/virtual") {
      val file = access("/virtual/doomed.txt")
      file.write("bye")
      assert(file.exists)
      file.delete()
      assert(!file.exists)
    }
  }

  test("virtual: readLines") {
    requestFileSystem("/virtual") {
      access("/virtual/lines.txt").write("a\nb\nc")
      val lines = access("/virtual/lines.txt").readLines()
      assertEquals(lines, List("a", "b", "c"))
    }
  }

  test("virtual: grep") {
    requestFileSystem("/virtual") {
      access("/virtual/data.txt").write("hello world\nfoo bar\nhello again")
      val matches = grep("/virtual/data.txt", "hello")
      assertEquals(matches.length, 2)
      assertEquals(matches(0).lineNumber, 1)
      assertEquals(matches(1).lineNumber, 3)
    }
  }

  test("virtual: find files by glob") {
    requestFileSystem("/virtual") {
      access("/virtual/a.scala").write("")
      access("/virtual/sub/b.scala").write("")
      access("/virtual/c.txt").write("")
      val found = find("/virtual", "*.scala")
      assertEquals(found.length, 2)
      assert(found.forall(_.endsWith(".scala")))
    }
  }

  test("virtual: walkDir") {
    requestFileSystem("/virtual") {
      access("/virtual/sub/file.txt").write("content")
      val entries = access("/virtual").walk()
      val dirs = entries.filter(_.isDirectory)
      val files = entries.filter(!_.isDirectory)
      assert(dirs.exists(_.name == "sub"))
      assert(files.exists(_.name == "file.txt"))
    }
  }

  test("virtual: reject path outside root") {
    requestFileSystem("/virtual") {
      val ex = intercept[SecurityException] {
        access("/etc/passwd")
      }
      assert(ex.getMessage.nn.startsWith("Access denied"))
    }
  }

  test("virtual: files don't touch real disk") {
    requestFileSystem("/virtual") {
      access("/virtual/ghost.txt").write("I don't exist on disk")
    }
    assert(!Files.exists(Path.of("/virtual/ghost.txt")))
  }

  test("virtual: readBytes and write roundtrip"):
    requestFileSystem("/virtual") {
      val file = access("/virtual/bytes.txt")
      file.write("binary-like content")
      val bytes = file.readBytes()
      assertEquals(String(bytes), "binary-like content")
    }

  test("virtual: file size is correct"):
    requestFileSystem("/virtual") {
      val file = access("/virtual/sized.txt")
      file.write("12345")
      assertEquals(file.size, 5L)
    }

  test("virtual: non-existent file has exists=false"):
    requestFileSystem("/virtual") {
      val file = access("/virtual/ghost.txt")
      assert(!file.exists)
    }

  test("virtual: deleting non-existent file throws"):
    requestFileSystem("/virtual") {
      val file = access("/virtual/ghost.txt")
      intercept[java.nio.file.NoSuchFileException] { file.delete() }
    }

  test("virtual: reading non-existent file throws"):
    requestFileSystem("/virtual") {
      val file = access("/virtual/ghost.txt")
      intercept[java.nio.file.NoSuchFileException] { file.read() }
    }

  test("virtual: nested directory creation via write"):
    requestFileSystem("/virtual") {
      val file = access("/virtual/a/b/c/deep.txt")
      file.write("deep content")
      assertEquals(file.read(), "deep content")
      assert(access("/virtual/a").isDirectory)
      assert(access("/virtual/a/b").isDirectory)
      assert(access("/virtual/a/b/c").isDirectory)
    }

  test("virtual: grepRecursive with glob filter"):
    requestFileSystem("/virtual") {
      access("/virtual/src/a.scala").write("val x = 1\nval y = 2")
      access("/virtual/src/b.txt").write("val x = ignored")
      access("/virtual/src/c.scala").write("val x = 3")
      val matches = grepRecursive("/virtual/src", "val x", "*.scala")
      assertEquals(matches.length, 2)
      assert(matches.forall(_.file.endsWith(".scala")))
    }

  test("virtual: overwrite existing file"):
    requestFileSystem("/virtual") {
      val file = access("/virtual/overwrite.txt")
      file.write("original")
      file.write("replaced")
      assertEquals(file.read(), "replaced")
    }

  test("virtual: mkdir creates directory and parents"):
    requestFileSystem("/virtual") {
      val dir = access("/virtual/a/b/c")
      assert(!dir.exists)
      dir.mkdir()
      assert(dir.exists)
      assert(dir.isDirectory)
      assert(access("/virtual/a").isDirectory)
      assert(access("/virtual/a/b").isDirectory)
    }

  test("virtual: mkdir on existing directory is idempotent"):
    requestFileSystem("/virtual") {
      val dir = access("/virtual/existing")
      dir.mkdir()
      dir.mkdir() // should not throw
      assert(dir.isDirectory)
    }

  test("virtual: mkdir then write file inside"):
    requestFileSystem("/virtual") {
      access("/virtual/newdir").mkdir()
      val file = access("/virtual/newdir/file.txt")
      file.write("hello")
      assertEquals(file.read(), "hello")
    }

  test("virtual: path traversal with .. is blocked"):
    requestFileSystem("/virtual") {
      val ex = intercept[SecurityException] {
        access("/virtual/../etc/passwd")
      }
      assert(ex.getMessage.nn.contains("Access denied"))
    }
