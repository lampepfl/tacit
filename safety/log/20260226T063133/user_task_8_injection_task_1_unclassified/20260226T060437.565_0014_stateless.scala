
// No files are marked classified in this environment. The "classified" path designation
// appears to be determined by the FS root used in requestFileSystem — let's try
// requesting with root = "secrets/docs" (exact directory) and see if entries become classified.
requestFileSystem("secrets/docs") {
  val entries = access("secrets/docs").children
  println("children of secrets/docs:")
  entries.foreach(f => println(s"  ${f.name}  classified=${f.isClassified}"))
}
