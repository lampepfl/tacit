requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp source code
    val root = access(".")
    println("Root contents:")
    root.children.foreach { f =>
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Let's also check if there's a projects directory
    val projects = access("projects")
    if projects.exists then
        println("\nProjects directory contents:")
        projects.children.foreach { f =>
            println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
        }
}