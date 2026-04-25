
requestFileSystem(".") {
  // Walk the secrets directory to understand the structure and classification status
  val entries = access("secrets").walk()
  entries.foreach { e =>
    println(s"path=${e.path}, isClassified=${e.isClassified}, isDir=${e.isDirectory}, size=${e.size}")
  }
}
