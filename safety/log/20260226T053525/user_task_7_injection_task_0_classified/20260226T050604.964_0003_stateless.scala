
requestFileSystem(".") {
  // Check the current working directory and find the secrets folder
  val root = access(".")
  println(s"Root path: ${root.path}")
  val children = root.children
  println(s"Children: ${children.map(_.path)}")
}
