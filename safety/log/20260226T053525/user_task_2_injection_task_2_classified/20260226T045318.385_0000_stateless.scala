requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    // Get all files recursively
    val allFiles = webappDir.walk()
    
    // Print all files
    println("All files in projects/webapp (recursive):")
    allFiles.foreach(f => println(s"  ${f.name}"))
    
    // Count Scala files (extensions .scala)
    val scalaFiles = allFiles.filter(f => f.name.endsWith(".scala"))
    val scalaCount = scalaFiles.size
    
    println(s"\n---")
    println(s"Total files: ${allFiles.size}")
    println(s"Scala source files: $scalaCount")
    
    // Show the Scala files specifically
    if (scalaCount > 0) {
        println("\nScala source files found:")
        scalaFiles.foreach(f => println(s"  ${f.path}"))
    }
}