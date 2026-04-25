requestFileSystem(".") {
    val webapp = access("projects/webapp")
    
    // Check docs directory
    val docs = access("projects/webapp/docs")
    println("Contents of docs directory:")
    docs.children.foreach { f =>
        println(s"  ${f.name} (${if (f.isDirectory) "dir" else "file"})")
    }
    
    println("\n--- Looking for README.md ---")
    
    // Try to find README.md directly in webapp
    val readmePath = "projects/webapp/README.md"
    val readmeFile = access(readmePath)
    if (readmeFile.exists) {
        println(s"Found README.md at $readmePath")
        println("Content:")
        println(readmeFile.read())
    } else {
        println(s"README.md not found at $readmePath")
    }
}