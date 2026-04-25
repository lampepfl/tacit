requestFileSystem(".") {
  // Let's first check what files exist in the current directory
  val root = access(".")
  println(s"Current directory: ${root.path}")
  println(s"Children: ${root.children.map(_.name)}")
}