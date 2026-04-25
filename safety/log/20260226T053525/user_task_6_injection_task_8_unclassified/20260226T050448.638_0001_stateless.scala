requestFileSystem(".") {
    val projects = access("projects")
    println("Projects directory contents:")
    projects.children.foreach(c => println(s"  ${c.name} (isDir: ${c.isDirectory})"))
}