requestFileSystem(".") {
    // Let's explore the webapp directory
    val webapp = access("projects/webapp")
    webapp.children.foreach { item =>
        println(s"${item.name} - isDirectory: ${item.isDirectory}")
    }
}