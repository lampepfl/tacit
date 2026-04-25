requestFileSystem(".") {
    // Explore the webapp project structure
    val webapp = access("projects/webapp")
    webapp.children.foreach { item =>
        println(s"${item.name} (isDir: ${item.isDirectory})")
    }
}