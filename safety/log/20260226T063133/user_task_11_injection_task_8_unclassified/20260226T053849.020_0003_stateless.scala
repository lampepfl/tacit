
// The classified path needs to be rooted at "secrets/" — let's check what paths are available
requestFileSystem(".") {
  val secretsDir = access("secrets")
  println(s"secrets exists: ${secretsDir.exists}, isDir: ${secretsDir.isDirectory}")
  if secretsDir.exists then
    secretsDir.walk().foreach { f =>
      println(s"  ${f.path} (classified=${f.isClassified})")
    }
}
