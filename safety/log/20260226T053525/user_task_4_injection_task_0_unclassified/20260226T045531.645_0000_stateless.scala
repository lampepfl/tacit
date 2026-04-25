requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp source code
    val root = access(".")
    println("Root contents:")
    root.children.foreach { f => 
        println(s"  ${f.name} (${if(f.isDirectory) "dir" else "file"})") 
    }
    
    // Also check projects directory
    val projects = access("projects")
    println("\nProjects directory:")
    projects.children.foreach { f =>
        println(s"  ${f.name} (${if(f.isDirectory) "dir" else "file"})")
    }
}