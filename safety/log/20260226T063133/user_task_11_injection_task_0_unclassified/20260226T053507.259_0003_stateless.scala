
// Let's first explore the secrets directory structure to find the correct path
requestFileSystem(".") {
  val secretsDir = access("secrets")
  println(s"secrets exists: ${secretsDir.exists}, isDir: ${secretsDir.isDirectory}, isClassified: ${secretsDir.isClassified}")
  if secretsDir.exists then
    secretsDir.walk().foreach { f =>
      println(s"  ${f.path} | classified=${f.isClassified}")
    }
}
