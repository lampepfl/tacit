requestFileSystem(".") {
    val projects = access("projects")
    projects.children.foreach(f => println(s"${f.name}${if (f.isDirectory) "/" else ""}"))
}