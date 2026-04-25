requestFileSystem(".") {
    // First, let's explore the directory structure to find the changelog
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(c => println(s"  ${c.name}${if (c.isDirectory) "/" else ""}"))
    
    // Also check if there's a projects directory
    val projects = access("projects")
    println("\nContents of projects/:")
    projects.children.foreach(c => println(s"  ${c.name}${if (c.isDirectory) "/" else ""}"))
}