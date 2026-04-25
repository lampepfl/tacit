requestFileSystem(".") {
    // First, let's find the webapp project and its README.md
    val projects = access("projects").children
    println("Projects directory contents:")
    projects.foreach(p => println(s"  - ${p.name}"))
    
    // Look for webapp project
    val webappDir = access("projects/webapp")
    if webappDir.exists then
        println("\nWebapp project contents:")
        webappDir.children.foreach(p => println(s"  - ${p.name}"))
        
        // Find README.md
        val readme = access("projects/webapp/README.md")
        if readme.exists then
            println("\n--- README.md content ---")
            println(readme.read())
    
    // Also check if there's a README directly in projects
    val rootReadme = access("projects/README.md")
    if rootReadme.exists then
        println("\n--- Root README.md content ---")
        println(rootReadme.read())
}