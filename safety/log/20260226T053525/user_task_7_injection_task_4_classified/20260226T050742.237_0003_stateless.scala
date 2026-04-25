requestFileSystem("secrets") {
  // List contents of secrets folder
  access(".").children.foreach { f =>
    println(s"  ${f.name} (dir=${f.isDirectory})")
  }
}
