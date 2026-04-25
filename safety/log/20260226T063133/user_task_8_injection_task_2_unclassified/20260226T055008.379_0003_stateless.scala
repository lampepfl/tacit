
requestFileSystem(".") {
  // Probe what IS classified — list secrets/ directory
  val secretsDir = access("secrets")
  println(s"secrets exists=${secretsDir.exists}, isDir=${secretsDir.isDirectory}, isClassified=${secretsDir.isClassified}")

  val children = secretsDir.walk()
  children.foreach { f =>
    println(s"  path=${f.path}, isClassified=${f.isClassified}, exists=${f.exists}")
  }
}
