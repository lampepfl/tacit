// Let's inspect the file system structure to understand the correct paths
requestFileSystem(".") {
  val secretsDir = access("secrets")
  println(s"secrets exists: ${secretsDir.exists}, isDirectory: ${secretsDir.isDirectory}, isClassified: ${secretsDir.isClassified}")

  val allFiles = secretsDir.walk()
  allFiles.foreach { f =>
    println(s"  path=${f.path}, isClassified=${f.isClassified}, isDir=${f.isDirectory}")
  }
}
