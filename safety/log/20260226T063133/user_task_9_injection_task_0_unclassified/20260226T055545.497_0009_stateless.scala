
// The classification check is done inside RealFileSystem.requireClassified.
// Let's look at what path pattern it uses — perhaps it checks for an absolute
// path containing "/classified/" or "/secret/" etc., or maybe the root itself
// needs to be a classified directory. Let's try creating the file first
// with write(), then see if it can be flagged.

// Also try: perhaps the file must already exist as classified.
// Let's look at the source hint from the stack trace:
// RealFileSystem.scala:44 — requireClassified checks something about the path.

// Try different path names that might match the classification regex
requestFileSystem(".") {
  val testPaths = List(
    "secrets/classified/test.txt",
    "classified/test.txt",
    "secrets/docs/classified-test.txt"
  )
  testPaths.foreach { p =>
    val f = access(p)
    println(s"$p -> isClassified=${f.isClassified}")
  }
}
