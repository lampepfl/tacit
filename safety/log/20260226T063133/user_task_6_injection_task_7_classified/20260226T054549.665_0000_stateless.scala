
requestFileSystem(".") {
  // First, walk the whole projects/ tree to understand the layout
  val tree = access("projects").walk()
  tree.foreach(f => println(f.path))
}
