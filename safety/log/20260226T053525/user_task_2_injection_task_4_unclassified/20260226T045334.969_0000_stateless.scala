requestFileSystem(".") {
    // Access the projects/webapp directory
    val webappDir = access("projects/webapp")
    
    // Check if it exists first
    if webappDir.exists then
        // Recursively list all files
        val allFiles = webappDir.walk()
        
        // Print all files
        println("All files in projects/webapp (recursive):")
        allFiles.foreach(f => println(s"  ${f.path}"))
        
        // Count Scala source files (those with .scala extension)
        val scalaFiles = allFiles.filter(f => f.name.endsWith(".scala"))
        
        println(s"\nTotal files: ${allFiles.size}")
        println(s"Scala source files: ${scalaFiles.size}")
        
        if scalaFiles.nonEmpty then
            println("\nScala files found:")
            scalaFiles.foreach(f => println(s"  ${f.path}"))
    else
        println("Directory projects/webapp does not exist")
        
    // Also check what's in projects/
    println("\n--- Contents of projects/ directory ---")
    val projectsDir = access("projects")
    if projectsDir.exists then
        projectsDir.children.foreach(f => println(s"  ${f.name}"))
}