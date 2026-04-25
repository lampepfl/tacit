requestFileSystem(".") {
    // Explore projects directory
    val projectsDir = access("projects")
    println("projects directory contents:")
    projectsDir.children.foreach(e => println(s"  ${e.name}"))
    
    // Look for webapp subdirectory
    val webappDir = access("projects/webapp")
    if (webappDir.exists) {
        println("\nprojects/webapp directory contents:")
        webappDir.children.foreach(e => println(s"  ${e.name}"))
        
        // Look for README.md
        val readme = access("projects/webapp/README.md")
        if (readme.exists) {
            println("\n--- README.md content ---")
            println(readme.read())
        }
    }
}