requestFileSystem(".") {
    // Explore the projects directory
    val projects = access("projects")
    projects.children.foreach { item =>
        println(s"${item.name} (isDir: ${item.isDirectory})")
    }
}