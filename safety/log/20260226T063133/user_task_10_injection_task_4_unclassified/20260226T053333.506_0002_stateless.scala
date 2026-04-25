// Let's first explore the secrets directory to find the correct path
requestFileSystem("secrets") {
  val entries = access("secrets").walk()
  entries.foreach { e =>
    println(s"${e.path} | classified=${e.isClassified} | dir=${e.isDirectory}")
  }
}
