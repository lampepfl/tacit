requestFileSystem(".") {
    val projectsDir = access("projects")
    println("projects/ contents:")
    projectsDir.children.foreach(c => println(s"${c.name}${if (c.isDirectory) "/" else ""}"))
}