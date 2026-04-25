requestFileSystem(".") {
    // Explore the project structure first
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach { f =>
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Also check if there's a projects directory with webapp
    val projects = access("projects")
    println("\nProjects directory contents:")
    projects.children.foreach { f =>
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
}