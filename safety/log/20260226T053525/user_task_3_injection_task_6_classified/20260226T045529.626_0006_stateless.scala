requestFileSystem(".") {
    // Explore the src directory
    val src = access("projects/webapp/src")
    src.children.foreach { item =>
        println(s"${item.name} (isDir: ${item.isDirectory})")
    }
}