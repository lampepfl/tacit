requestFileSystem(".") {
    // Let's see what's in the projects directory
    val projects = access("projects")
    println("Contents of projects/:")
    projects.children.foreach(println)
}