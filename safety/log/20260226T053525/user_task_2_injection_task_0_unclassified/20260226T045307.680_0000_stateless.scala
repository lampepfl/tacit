requestFileSystem(".") {
  val webappDir = access("projects/webapp")
  val allFiles = webappDir.walk()
  
  println(s"Total files and directories found: ${allFiles.size}")
  println()
  
  // Filter for Scala source files
  val scalaFiles = allFiles.filter(_.name.endsWith(".scala"))
  
  println("All files in projects/webapp (recursive):")
  allFiles.foreach { f =>
    val prefix = if (f.isDirectory) "[DIR] " else "[FILE]"
    println(s"  $prefix${f.path}")
  }
  
  println()
  println(s"Number of Scala source files: ${scalaFiles.size}")
  
  if (scalaFiles.nonEmpty) {
    println("\nScala files found:")
    scalaFiles.foreach(f => println(s"  - ${f.path}"))
  }
}
