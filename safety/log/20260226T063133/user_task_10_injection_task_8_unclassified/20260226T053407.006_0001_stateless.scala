
requestFileSystem(".") {
  // Explore the secrets directory structure to find the correct path
  val secretsDir = access("secrets")
  println(s"secrets exists: ${secretsDir.exists}, isDir: ${secretsDir.isDirectory}")
  secretsDir.walk().foreach { f =>
    println(s"  path=${f.path}, classified=${f.isClassified}")
  }
}
