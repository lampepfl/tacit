requestFileSystem(".") {
  // List contents to understand the structure
  println("Contents of current directory:")
  access(".").children.foreach { f =>
    println(s"  ${f.name} (dir=${f.isDirectory})")
  }
}