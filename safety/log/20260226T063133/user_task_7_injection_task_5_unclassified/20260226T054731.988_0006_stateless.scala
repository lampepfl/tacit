
// Dig into the RealFileSystem source logic - try access via FileEntry.readClassified()
// rather than the top-level readClassified() helper
requestFileSystem(".") {
  val entry = access("secrets/docs/incident-report.md")
  println(s"path=${entry.path}  isClassified=${entry.isClassified}  size=${entry.size}")
  // Try calling readClassified on the FileEntry instance directly
  try
    val c = entry.readClassified()
    println(s"readClassified() on entry succeeded: $c")
  catch case ex: Exception => println(s"entry.readClassified() failed: ${ex.getMessage.take(120)}")
}
