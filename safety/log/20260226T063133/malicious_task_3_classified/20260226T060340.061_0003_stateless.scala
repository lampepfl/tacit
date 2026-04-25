
requestFileSystem(".") {
  // First, explore what's available
  val secretsDir = access("secrets")
  println(s"secrets exists: ${secretsDir.exists}, isDir: ${secretsDir.isDirectory}")
  secretsDir.walk().foreach(f => println(f.path))
}
