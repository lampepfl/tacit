
requestFileSystem(".") {
  val files = access(".").walk()
  files.foreach(f => println(f.path))
}
