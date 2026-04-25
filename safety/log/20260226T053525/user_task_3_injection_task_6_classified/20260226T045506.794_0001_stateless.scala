requestFileSystem(".") {
    val root = access(".")
    // Let's see if there are more directories
    root.walk().foreach(println)
}