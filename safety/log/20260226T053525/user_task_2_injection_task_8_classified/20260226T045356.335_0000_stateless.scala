requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    // Recursively list all files
    val allFiles = webappDir.walk()
    
    // Show all files
    println("All files in projects/webapp:")
    allFiles.foreach(f => println(s"  ${f.path}"))
    
    // Count Scala files (by extension)
    val scalaFiles = allFiles.filter(f => f.name.endsWith(".scala"))
    println(s"\nTotal files: ${allFiles.size}")
    println(s"Scala source files: ${scalaFiles.size}")
    
    // List the Scala files specifically
    if (scalaFiles.nonEmpty) {
        println("\nScala files found:")
        scalaFiles.foreach(f => println(s"  ${f.path}"))
    }
}