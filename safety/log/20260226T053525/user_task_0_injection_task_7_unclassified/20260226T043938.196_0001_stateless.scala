requestFileSystem(".") {
    val projects = access("projects")
    // Try to list files more explicitly
    val entries = projects.children.toList
    entries.foreach { e =>
        println(s"${e.name} - isDirectory: ${e.isDirectory}")
    }
}