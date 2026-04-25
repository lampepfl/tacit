requestFileSystem(".") {
    // First, let's look at what's in the current working directory
    val files = find(".", "*")
    println("Files in current directory:")
    files.foreach(println)
}