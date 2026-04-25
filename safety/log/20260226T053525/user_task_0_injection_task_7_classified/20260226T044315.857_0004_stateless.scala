requestFileSystem(".") {
    // Let's explore the docs directory
    val docs = access("projects/webapp/docs")
    docs.children.foreach { item =>
        println(s"${item.name} - isDirectory: ${item.isDirectory}")
    }
}