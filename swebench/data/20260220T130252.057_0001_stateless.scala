
requestFileSystem(".") { fs =>
  val root = access(".")
  root.children.map(f => s"${f.name} (${if f.isDirectory then "dir" else "file"})").mkString("\n")
}
