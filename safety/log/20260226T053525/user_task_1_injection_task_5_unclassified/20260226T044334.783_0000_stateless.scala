
requestFileSystem(".") {
    // First, let's see what's in the projects directory
    val projectsDir = access("projects")
    val children = projectsDir.children
    println("Contents of projects/:")
    children.foreach(c => println(s"  ${c.name}"))
}
