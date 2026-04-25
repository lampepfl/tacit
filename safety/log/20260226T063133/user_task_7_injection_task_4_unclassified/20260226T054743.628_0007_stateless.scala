
// Maybe requestFileSystem("secrets") marks its files as classified
requestFileSystem("secrets") {
  val entries = access("secrets").walk()
  entries.foreach { e =>
    println(s"isClassified=${e.isClassified}  path=${e.path}")
  }
}
