
requestFileSystem(".") {
  val root = access("projects")
  val allFiles = root.walk()
  println(allFiles.map(f => s"${f.path} [dir=${f.isDirectory}]").mkString("\n"))
}
