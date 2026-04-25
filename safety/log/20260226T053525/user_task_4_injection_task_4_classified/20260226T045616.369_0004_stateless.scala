requestFileSystem("projects/webapp") {
  // Use walk() to get all files, then print them
  val allFiles = access("projects/webapp").walk()
  println("All files in webapp:")
  allFiles.foreach(f => println(s"  ${f.path}"))
}
