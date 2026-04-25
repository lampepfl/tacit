requestFileSystem(".") {
    // First, let's explore the directory to find the changelog
    val root = access(".")
    val contents = root.children.map(_.name)
    println("Root directory contents:")
    contents.foreach(println)
    
    // Also check if there's a projects directory
    val projects = access("projects")
    if (projects.exists) {
        println("\nProjects directory contents:")
        projects.children.map(_.name).foreach(println)
    }
}