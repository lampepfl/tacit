requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { item =>
        println(s"  ${item.name}${if (item.isDirectory) "/" else ""}")
    }
    
    // Let's also check if there's a projects directory
    val projectsDir = access("projects")
    if (projectsDir.exists) {
        println("\nContents of projects/ directory:")
        projectsDir.children.foreach { item =>
            println(s"  ${item.name}${if (item.isDirectory) "/" else ""}")
        }
    }
}