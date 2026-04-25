
requestFileSystem(".") {
  // First, let's explore the overall structure
  val root = access("projects").walk()
  root.foreach(f => println(s"${f.path}  [dir=${f.isDirectory}]"))
}
