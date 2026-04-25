requestFileSystem(".") {
    // Let's look at the root directory contents more explicitly
    val root = access(".")
    root.children.foreach { item =>
        println(s"${item.name} - isDirectory: ${item.isDirectory}")
    }
}