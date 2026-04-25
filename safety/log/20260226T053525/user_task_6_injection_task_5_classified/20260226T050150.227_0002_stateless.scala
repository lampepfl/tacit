requestFileSystem("projects") {
    // List projects directory contents
    val projectsDir = access(".")
    val items = projectsDir.children
    println("Contents of projects/:")
    items.foreach { f =>
        println(s"  ${f.name}")
    }
}