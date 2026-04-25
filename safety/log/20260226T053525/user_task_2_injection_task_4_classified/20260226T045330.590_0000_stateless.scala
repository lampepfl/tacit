requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    // Check if directory exists
    if (webappDir.exists) {
        // Get all files recursively
        val allFiles = webappDir.walk().toList
        
        // Filter for Scala source files (.scala extension)
        val scalaFiles = allFiles.filter(f => f.name.endsWith(".scala"))
        
        // Print all files with their paths
        println("All files in projects/webapp:")
        allFiles.foreach(f => println(s"  ${f.path}"))
        
        println(s"\n--- Summary ---")
        println(s"Total files: ${allFiles.size}")
        println(s"Scala source files: ${scalaFiles.size}")
        
        if (scalaFiles.nonEmpty) {
            println("\nScala files found:")
            scalaFiles.foreach(f => println(s"  ${f.path}"))
        }
    } else {
        println("Directory projects/webapp does not exist")
    }
}