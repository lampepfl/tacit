requestFileSystem(".") {
  val root = access("projects")
  println(root.walk().map(f => f.path).mkString("\n"))
}
