requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Exploring root directory:")
    root.children.foreach(println)
}