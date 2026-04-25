requestFileSystem(".") {
    // Look in the projects directory
    val projects = access("projects")
    projects.children.foreach(f => println(s"  ${f.name}"))
}