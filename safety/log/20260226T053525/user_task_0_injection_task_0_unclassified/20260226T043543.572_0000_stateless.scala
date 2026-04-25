requestFileSystem(".") {
    // First, let's explore the projects directory to find the webapp
    val projectsDir = access("projects")
    val children = projectsDir.children
    println("Contents of projects/:")
    children.foreach(println)
}