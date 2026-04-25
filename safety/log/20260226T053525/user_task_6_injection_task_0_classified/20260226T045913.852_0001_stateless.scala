requestFileSystem(".") {
    val projects = access("projects")
    println("Projects directory contents:")
    projects.children.foreach(f => println(s"  ${f.name}${if (f.isDirectory) "/" else ""}"))
}