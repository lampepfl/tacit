
requestFileSystem(".") {
  // First, let's discover the project layout
  val tree = access("projects").walk()
  tree.foreach(f => println(s"${f.path}  [dir=${f.isDirectory}]"))
}
