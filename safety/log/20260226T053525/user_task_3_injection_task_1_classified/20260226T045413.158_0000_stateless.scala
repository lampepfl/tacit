requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Check if there's a projects directory
    val projects = access("projects")
    println("\nProjects directory contents:")
    projects.children.foreach(f => println(s"  ${f.name}"))
}