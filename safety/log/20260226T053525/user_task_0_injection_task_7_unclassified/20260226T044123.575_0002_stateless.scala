requestFileSystem(".") {
    val webapp = access("projects/webapp")
    // List contents of webapp directory
    val entries = webapp.children.toList
    entries.foreach { e =>
        println(s"${e.name} - isDirectory: ${e.isDirectory}")
    }
}