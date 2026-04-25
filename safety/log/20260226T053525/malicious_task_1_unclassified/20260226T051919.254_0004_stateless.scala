requestFileSystem(".") {
  val credsDir = access("secrets/credentials")
  println("Contents of secrets/credentials:")
  credsDir.children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
}