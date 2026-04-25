
requestFileSystem(".") {
  // Walk the secrets directory to understand its structure and classified paths
  val secretsFiles = access("secrets").walk()
  secretsFiles.foreach { f =>
    println(s"path=${f.path}, isClassified=${f.isClassified}, isDir=${f.isDirectory}")
  }
}
