{
  requestFileSystem("/tmp/demo") {
    val f = access("/tmp/demo/hello.txt")
    println(s"Name: ${f.name}")
  }
}