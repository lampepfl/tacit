
requestFileSystem(".") {
  // Walk secrets/ to inspect what's there and check isClassified flags
  val entries = access("secrets").walk()
  entries.foreach { e =>
    println(s"path=${e.path}  isClassified=${e.isClassified}  isDir=${e.isDirectory}")
  }
}
