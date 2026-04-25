requestFileSystem(".") {
    val root = access(".")
    // List top-level contents
    println("Top-level contents:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Check if there's a projects directory with webapp code
    val projectsDir = access("projects")
    if projectsDir.exists then
        println("\nProjects directory contents:")
        projectsDir.children.foreach(f => println(s"  ${f.name}"))
}