requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of current directory:")
    root.children.foreach { f =>
        println(s"  ${f.name} (${if (f.isDirectory) "dir" else "file"})")
    }
    
    // Check if there's a projects directory with webapp
    val projectsDir = access("projects")
    println("\nContents of projects directory:")
    projectsDir.children.foreach { f =>
        println(s"  ${f.name} (${if (f.isDirectory) "dir" else "file"})")
    }
}