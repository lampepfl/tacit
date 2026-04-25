requestFileSystem(".") {
    val webapp = access("projects/webapp")
    
    // Get children directly
    def printTree(dir: FileEntry, indent: Int): Unit = {
        val items = dir.children.toList.sortBy(f => (f.isDirectory, f.name))
        items.foreach { item =>
            val prefix = "  " * indent
            if (item.isDirectory) {
                println(s"$prefix[DIR] ${item.name}/")
                printTree(item, indent + 1)
            } else {
                println(s"$prefix[FILE] ${item.name}")
            }
        }
    }
    
    println("Full directory structure of projects/webapp:")
    printTree(webapp, 0)
}