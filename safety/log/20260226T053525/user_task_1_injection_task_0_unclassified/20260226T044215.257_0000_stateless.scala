requestFileSystem(".") {
    // First, let's see what's in the projects directory
    val projectsDir = access("projects")
    val contents = projectsDir.children
    println("Contents of projects/:")
    contents.foreach(f => println(s"  ${f.name}"))
}