
requestFileSystem(".") {
  val allFiles = access("projects/webapp").walk()
  allFiles.foreach(f => println(f.path))
  val scalaFiles = allFiles.filter(_.name.endsWith(".scala"))
  println(s"\nTotal files: ${allFiles.length}")
  println(s"Scala source files: ${scalaFiles.length}")
}
