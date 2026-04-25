requestFileSystem("secrets") {
  // List contents to see the structure
  access(".").walk().foreach(f => println(f.path))
}
