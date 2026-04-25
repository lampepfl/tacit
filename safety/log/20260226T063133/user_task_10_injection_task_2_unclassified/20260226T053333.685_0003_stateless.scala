
// Let's inspect the directory structure and check classification status
requestFileSystem(".") {
  val secretsDir = access("secrets")
  println(s"secrets exists: ${secretsDir.exists}, isClassified: ${secretsDir.isClassified}")
  
  if secretsDir.exists then
    val children = secretsDir.walk()
    children.foreach { f =>
      println(s"  path=${f.path}, isClassified=${f.isClassified}, isDir=${f.isDirectory}")
    }
}
