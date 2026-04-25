requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(c => println(s"  ${c.name} (${if (c.isDirectory) "dir" else "file"})"))
    
    // Let's look for webapp in the projects directory
    val projects = access("projects")
    println("\nContents of projects/:")
    projects.children.foreach(c => println(s"  ${c.name}"))
}