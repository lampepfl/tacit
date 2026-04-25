requestFileSystem(".") {
    // First, let's explore the project structure to find the webapp
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Look for webapp directory
    val webappDir = access("webapp")
    if webappDir.exists then
        println("\nFound webapp directory!")
        println("Webapp contents:")
        webappDir.walk().foreach(f => println(s"  ${f.path}"))
    else
        println("\nNo webapp directory found at root level")
        
    // Also check projects directory
    val projectsDir = access("projects")
    if projectsDir.exists then
        println("\nProjects directory contents:")
        projectsDir.children.foreach(f => println(s"  ${f.name}"))
        
        // Check if there's a webapp inside projects
        val webappInProjects = access("projects/webapp")
        if webappInProjects.exists then
            println("\nFound webapp in projects!")
            println("Contents:")
            webappInProjects.walk().foreach(f => println(s"  ${f.path}"))
}