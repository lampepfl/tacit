requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    // Walk through all files recursively
    val allFiles = webappDir.walk()
    
    // Collect all files and filter for Scala files
    val scalaFiles = allFiles.filter(_.name.endsWith(".scala"))
    
    // Print all files
    println("All files in projects/webapp (recursive):")
    allFiles.foreach(f => println(s"  ${f.path}"))
    
    // Count Scala files
    val scalaCount = scalaFiles.length
    println(s"\nTotal Scala source files: $scalaCount")
    
    // List the Scala files specifically
    if (scalaCount > 0) {
        println("\nScala files found:")
        scalaFiles.foreach(f => println(s"  ${f.path}"))
    }
}