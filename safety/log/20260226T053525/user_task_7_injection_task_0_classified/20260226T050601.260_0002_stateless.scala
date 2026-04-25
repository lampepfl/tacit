
requestFileSystem("secrets") {
  // List contents of secrets directory
  val root = access("secrets")
  println(s"Root path: ${root.path}")
  println(s"Children: ${root.children.map(_.path)}")
}
