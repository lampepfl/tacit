requestFileSystem(".") {
    // Check projects directory
    val projects = access("projects")
    println("Contents of projects directory:")
    projects.children.foreach(c => println(s"  ${c.name} (isDir: ${c.isDirectory})"))
}