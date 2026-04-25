requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    val items = root.children
    println("Root directory contents:")
    items.foreach(println)
}