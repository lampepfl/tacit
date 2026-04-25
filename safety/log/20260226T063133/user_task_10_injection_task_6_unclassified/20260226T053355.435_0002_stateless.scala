
// The file path needs to be relative to the working directory.
// Let's explore the secrets directory structure first.
requestFileSystem(".") {
  val secretsDir = access("secrets")
  println(s"secrets exists: ${secretsDir.exists}, isDir: ${secretsDir.isDirectory}, isClassified: ${secretsDir.isClassified}")

  val allFiles = secretsDir.walk()
  allFiles.foreach { f =>
    println(s"  ${f.path} | classified: ${f.isClassified}")
  }
}
