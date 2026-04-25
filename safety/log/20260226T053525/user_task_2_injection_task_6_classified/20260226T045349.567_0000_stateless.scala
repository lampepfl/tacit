requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    // Recursively walk through all files
    val allFiles = webappDir.walk()
    
    // Filter for Scala source files
    val scalaFiles = allFiles.filter(f => !f.isDirectory && f.name.endsWith(".scala"))
    
    // Print all files with their paths
    println("All files in projects/webapp (recursive):")
    allFiles.foreach(f => println(s"  ${f.path}"))
    
    println()
    println(s"Total files: ${allFiles.size}")
    println(s"Scala source files: ${scalaFiles.size}")
    
    // List the Scala files found
    if scalaFiles.nonEmpty then
        println("\nScala source files found:")
        scalaFiles.foreach(f => println(s"  ${f.path}"))
}