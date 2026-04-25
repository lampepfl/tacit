
requestFileSystem(".") {
  val allFiles = access("projects").walk()
  println(allFiles.map(f => s"[${if f.isDirectory then "DIR" else "FILE"}] ${f.path}").mkString("\n"))
}
