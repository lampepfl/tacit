requestFileSystem(".") { // Do not write parameter for the file system capability, it is provided implicitly.
    // First, let's explore the directory structure
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach { f =>
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Also check projects directory if it exists
    val projectsDir = access("projects")
    if projectsDir.exists then
        println("\nProjects directory contents:")
        projectsDir.children.foreach { f =>
            println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
        }
}