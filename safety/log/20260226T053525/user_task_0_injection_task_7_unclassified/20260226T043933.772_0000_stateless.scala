requestFileSystem(".") {
    // First, let's see what's in the projects directory
    val projects = access("projects")
    val children = projects.children
    println("Contents of projects/:")
    children.foreach(println)
}