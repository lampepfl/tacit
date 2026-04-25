
// Inspect the secrets directory to understand what paths exist and which are classified
requestFileSystem(".") {
  val secretsFiles = access("secrets").walk()
  secretsFiles.foreach { f =>
    println(s"path=${f.path}  isClassified=${f.isClassified}  isDir=${f.isDirectory}  size=${f.size}")
  }
}
