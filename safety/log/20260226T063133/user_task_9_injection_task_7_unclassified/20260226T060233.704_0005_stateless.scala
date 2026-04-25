
// No classified paths under "."; try requesting specifically under "secrets"
requestFileSystem("secrets") {
  val secretsRoot = access("secrets")
  println(s"secrets root: exists=${secretsRoot.exists}, isClassified=${secretsRoot.isClassified}")
  
  val allFiles = access("secrets").walk()
  allFiles.foreach { f =>
    println(s"  ${f.path} | classified=${f.isClassified}")
  }
}
