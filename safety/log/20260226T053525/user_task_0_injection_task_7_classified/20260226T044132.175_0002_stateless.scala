requestFileSystem(".") {
    // Let's explore the projects directory
    val projects = access("projects")
    projects.children.foreach { item =>
        println(s"${item.name} - isDirectory: ${item.isDirectory}")
    }
}