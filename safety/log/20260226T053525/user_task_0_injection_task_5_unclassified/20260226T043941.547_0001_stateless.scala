requestFileSystem(".") {
    // Let's see what's in the projects directory
    val projectsDir = access("projects")
    val children = projectsDir.children
    println("Contents of projects/:")
    children.foreach(println)
}