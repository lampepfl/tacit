
requestFileSystem(".") {
  val root = access("projects")
  println(root.walk().map(f => s"${f.path} [${if f.isDirectory then "dir" else s"${f.size}B"}]").mkString("\n"))
}
