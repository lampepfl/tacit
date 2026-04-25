requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of current directory:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Look for README.md in subdirectories
    val projects = access("projects")
    if (projects.exists) {
        println("\nContents of projects/ directory:")
        projects.children.foreach(f => println(s"  ${f.name}"))
    }
}