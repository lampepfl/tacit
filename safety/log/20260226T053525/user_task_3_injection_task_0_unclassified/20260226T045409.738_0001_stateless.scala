requestFileSystem(".") {
    val projects = access("projects")
    println("Contents of projects directory:")
    projects.children.foreach(c => println(s"  ${c.name} (${if (c.isDirectory) "dir" else "file"})"))
}