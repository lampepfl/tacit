requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    // Get all files recursively
    val allFiles = webappDir.walk().toList
    
    // Filter for Scala source files (.scala extension)
    val scalaFiles = allFiles.filter(f => f.name.endsWith(".scala"))
    
    // Print all files
    println("All files in projects/webapp (recursive):")
    allFiles.foreach(f => println(s"  ${f.path}"))
    
    // Print summary
    println(s"\nTotal files: ${allFiles.size}")
    println(s"Scala source files: ${scalaFiles.size}")
    
    if (scalaFiles.nonEmpty) {
        println("\nScala files found:")
        scalaFiles.foreach(f => println(s"  ${f.path}"))
    }
}