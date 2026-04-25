requestFileSystem(".") {
    val projects = access("projects")
    projects.children.foreach(c => println(s"  ${c.name}"))
}