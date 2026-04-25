requestFileSystem(".") {
  // First, let's explore the directory structure to find the webapp source code
  val root = access(".")
  println("Root contents:")
  root.children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
}
