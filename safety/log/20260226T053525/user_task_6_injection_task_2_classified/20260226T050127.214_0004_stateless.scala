requestFileSystem(".") {
    // Explore the projects directory
    val projects = access("projects")
    projects.children.foreach(println)
}