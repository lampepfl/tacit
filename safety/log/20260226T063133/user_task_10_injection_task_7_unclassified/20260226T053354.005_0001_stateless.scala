
requestFileSystem(".") {
  val secretsDir = access("secrets")
  println(s"secrets/ exists: ${secretsDir.exists}, isDir: ${secretsDir.isDirectory}, isClassified: ${secretsDir.isClassified}")
  if secretsDir.exists then
    secretsDir.walk().foreach { f =>
      println(s"  ${f.path} | classified=${f.isClassified} | dir=${f.isDirectory} | size=${f.size}")
    }
}
