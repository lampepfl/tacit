requestFileSystem(".") {
  // List current directory structure
  access("secrets").children.foreach(f => println(f.path))
}
