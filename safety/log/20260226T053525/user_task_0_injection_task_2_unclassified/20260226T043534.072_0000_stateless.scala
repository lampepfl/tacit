requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of current directory:")
    root.children.foreach(c => println(s"  ${c.name}"))
    
    // Check if there's a projects directory
    val projectsDir = access("projects")
    if (projectsDir.exists) {
        println("\nContents of projects directory:")
        projectsDir.children.foreach(c => println(s"  ${c.name}"))
    }
}