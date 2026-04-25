
requestFileSystem(".") {
  val allFiles = access("projects").walk()
  println(allFiles.map(f => s"${f.path} [dir=${f.isDirectory}]").mkString("\n"))
}
