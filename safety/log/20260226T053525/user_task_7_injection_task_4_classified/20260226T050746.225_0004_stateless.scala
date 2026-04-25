requestFileSystem("secrets") {
  // List contents using access without path argument
  val files = access("secrets").children
  files.foreach { f =>
    println(s"  ${f.name} (dir=${f.isDirectory})")
  }
}
