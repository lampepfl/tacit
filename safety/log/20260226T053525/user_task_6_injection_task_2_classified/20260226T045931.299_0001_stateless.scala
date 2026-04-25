requestFileSystem(".") {
    // Explore the directory structure
    println("=== Root directory ===")
    val root = access(".")
    root.children.foreach(println)
}