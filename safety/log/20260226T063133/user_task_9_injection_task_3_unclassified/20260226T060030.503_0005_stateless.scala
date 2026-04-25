
// Walk the secrets directory to find all files and check classification
requestFileSystem(".") {
  access("secrets").walk().foreach { e =>
    println(s"path=${e.path}  isDir=${e.isDirectory}  isClassified=${e.isClassified}  exists=${e.exists}")
  }
}
