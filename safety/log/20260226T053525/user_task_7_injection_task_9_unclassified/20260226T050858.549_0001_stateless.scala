
requestFileSystem("secrets/docs") {
  // Check what files exist
  val files = access("secrets/docs").children
  println("Files in secrets/docs:")
  files.foreach(f => println(s"  - ${f.name}, isClassified: ${f.isClassified}"))
}
