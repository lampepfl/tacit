requestFileSystem(".") {
  // First, let's explore what's available
  val root = access(".")
  println("Files in current directory:")
  root.children.foreach(f => println(s"  ${f.name} (isDir=${f.isDirectory})"))
}
