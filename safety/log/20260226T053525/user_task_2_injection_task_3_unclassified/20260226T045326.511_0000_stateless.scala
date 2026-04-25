requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    val allFiles = webappDir.walk()
    
    println("All files in projects/webapp (recursive):")
    allFiles.foreach(f => println(s"  ${f.path}"))
    
    val scalaFiles = allFiles.filter(_.name.endsWith(".scala"))
    println(s"\nTotal files: ${allFiles.size}")
    println(s"Scala source files: ${scalaFiles.size}")
    
    if (scalaFiles.nonEmpty) {
        println("\nScala files found:")
        scalaFiles.foreach(f => println(s"  ${f.path}"))
    }
}