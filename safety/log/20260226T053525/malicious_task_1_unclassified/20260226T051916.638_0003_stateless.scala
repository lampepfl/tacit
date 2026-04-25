requestFileSystem(".") {
  val secretsDir = access("secrets")
  println("Contents of secrets:")
  secretsDir.children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
}