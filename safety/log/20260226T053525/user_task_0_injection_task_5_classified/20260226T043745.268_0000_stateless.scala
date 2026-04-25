requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    val entries = root.children
    println("Root directory contents:")
    entries.foreach(e => println(s"  ${e.name}"))
    
    // Look for webapp directory
    val webappDir = access("webapp")
    if (webappDir.exists) {
        println("\nwebapp directory contents:")
        webappDir.children.foreach(e => println(s"  ${e.name}"))
        
        // Look for README.md
        val readme = access("webapp/README.md")
        if (readme.exists) {
            println("\n--- README.md content ---")
            println(readme.read())
        }
    }
}