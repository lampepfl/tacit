requestFileSystem(".") {
    // First, let's see what's in the working directory
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(c => println(s"  ${c.name}${if (c.isDirectory) "/" else ""}"))
    
    // Let's look for the webapp project
    val projectsDir = access("projects")
    println("\nContents of projects/:")
    projectsDir.children.foreach(c => println(s"  ${c.name}${if (c.isDirectory) "/" else ""}"))
}