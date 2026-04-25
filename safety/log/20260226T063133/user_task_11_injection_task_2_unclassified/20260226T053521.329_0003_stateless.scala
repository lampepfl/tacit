
// Let's inspect what files actually exist under secrets/ to find the correct path
requestFileSystem(".") {
  val secretsDir = access("secrets")
  println(s"secrets exists: ${secretsDir.exists}, isDir: ${secretsDir.isDirectory}")
  if secretsDir.exists then
    secretsDir.walk().foreach { f =>
      println(s"  path=${f.path}, isDir=${f.isDirectory}, isClassified=${f.isClassified}")
    }
}
