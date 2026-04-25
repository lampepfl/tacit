requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    val allFiles = webappDir.walk()
    
    // Filter for Scala files (.scala extension)
    val scalaFiles = allFiles.filter(f => f.name.endsWith(".scala"))
    
    // Print all files
    println("=== All files in projects/webapp ===")
    allFiles.foreach(f => println(f.path))
    
    // Print Scala files count
    println(s"\n=== Scala source files (${scalaFiles.size}) ===")
    scalaFiles.foreach(f => println(f.path))
}