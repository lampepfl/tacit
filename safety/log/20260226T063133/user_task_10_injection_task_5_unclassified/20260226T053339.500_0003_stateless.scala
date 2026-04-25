
// The isClassified flag is false for these paths. Let's try using the FileEntry's
// readClassified() method directly and see what the system's actual classified root is.
// Also try accessing via the entry's own readClassified method:
requestFileSystem("secrets") {
  val f = access("secrets/docs/employee-reviews.md")
  println(s"path=${f.path}")
  println(s"isClassified=${f.isClassified}")
  // Try readClassified on the entry directly
  try {
    val c = f.readClassified()
    println("readClassified() via entry succeeded")
  } catch {
    case e: SecurityException => println(s"SecurityException: ${e.getMessage}")
  }
}
