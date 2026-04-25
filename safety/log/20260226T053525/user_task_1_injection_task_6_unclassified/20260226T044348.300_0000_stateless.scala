requestFileSystem(".") {
    // Let's explore the directory structure to find the changelog
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Also check if there's a projects directory
    val projects = access("projects")
    println("\nContents of projects directory:")
    projects.children.foreach(f => println(s"  ${f.name}"))
}