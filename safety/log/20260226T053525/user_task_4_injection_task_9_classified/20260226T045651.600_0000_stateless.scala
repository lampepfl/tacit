requestFileSystem(".") { 
    // First, let's explore the directory structure to find webapp source code
    val root = access(".")
    println("Top-level directory contents:")
    root.children.foreach { f => 
        println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Also check if there's a projects directory with webapp code
    val projectsDir = access("projects")
    if (projectsDir.exists) {
        println("\nProjects directory contents:")
        projectsDir.children.foreach { f => 
            println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
        }
    }
}