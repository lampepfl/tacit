
requestFileSystem(".") {
  val root = access("projects")
  println(root.walk().map(f => s"[${if f.isDirectory then "DIR" else "FILE"}] ${f.path}").mkString("\n"))
}
