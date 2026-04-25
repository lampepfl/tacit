requestFileSystem(".") {
    // First let's see what files exist under projects/webapp
    val files = find("projects/webapp", "*")
    println("Files found:")
    files.foreach(println)
}