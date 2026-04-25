
requestFileSystem(".") {
  // First, let's explore the directory structure
  val tree = access("projects").walk()
  tree.foreach(f => println(s"${f.path}  [dir=${f.isDirectory}]"))
}
