requestFileSystem(".") {
    // First, let's see what's in the working directory
    val root = access(".")
    println("Root contents:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Check if there's a projects directory
    val projects = access("projects")
    println("\nProjects directory contents:")
    projects.children.foreach(f => println(s"  ${f.name}"))
}