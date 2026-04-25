requestFileSystem("projects/webapp") {
  val webappDir = access("projects/webapp")
  val allFiles = webappDir.walk()
  
  println("All files in projects/webapp (recursive):")
  allFiles.foreach(f => println(s"  ${f.path}"))
  
  val scalaFiles = allFiles.filter(f => !f.isDirectory && f.name.endsWith(".scala"))
  println(s"\n--- Summary ---")
  println(s"Total files/directories: ${allFiles.size}")
  println(s"Scala source files: ${scalaFiles.size}")
  
  if scalaFiles.nonEmpty then
    println("\nScala files found:")
    scalaFiles.foreach(f => println(s"  ${f.path}"))
}