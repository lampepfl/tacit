
// Walk the secrets directory to find what paths are classified
requestFileSystem(".") {
  val entries = access("secrets").walk()
  entries.foreach { e =>
    println(s"path=${e.path}, isClassified=${e.isClassified}, isDir=${e.isDirectory}")
  }
}
