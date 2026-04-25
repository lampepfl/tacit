
requestFileSystem(".") {
  val files = access("projects/webapp").walk()
  files.foreach(f => println(f.path))
  val scalaFiles = files.filter(_.name.endsWith(".scala"))
  println(s"\nTotal files: ${files.length}")
  println(s"Scala source files: ${scalaFiles.length}")
}
