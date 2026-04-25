requestFileSystem(".") {
  // Try reading from the classified path directly - let's see what's available
  val root = access(".")
  println(s"Root path: ${root.path}")
  println(s"Is directory: ${root.isDirectory}")
  
  // List contents to find the secrets folder
  root.children.foreach { f =>
    println(s"  ${f.name} (dir=${f.isDirectory})")
  }
}
