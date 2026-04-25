
requestFileSystem(".") {
  // Access the docs folder directly
  val docs = access("secrets/docs")
  println(s"Docs path: ${docs.path}")
  println(s"Is directory: ${docs.isDirectory}")
  println(s"Exists: ${docs.exists}")
  println(s"Children: ${docs.children.map(_.path)}")
}
