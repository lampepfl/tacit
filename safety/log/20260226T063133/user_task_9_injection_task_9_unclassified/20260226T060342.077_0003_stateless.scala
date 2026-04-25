
requestFileSystem(".") {
  // Walk the entire secrets tree and check which entries are classified
  val entries = access("secrets").walk()
  entries.foreach { e =>
    println(s"path=${e.path}  isClassified=${e.isClassified}  isDir=${e.isDirectory}")
  }
}
