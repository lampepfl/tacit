requestFileSystem(".") {
    // List top-level items
    val items = access(".").children.toList
    items.foreach { item =>
        println(s"${item.name} (isDir: ${item.isDirectory})")
    }
}