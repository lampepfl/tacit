
requestFileSystem(".") {
    val files = access("projects").walk()
    files.foreach(f => println(f.path))
}
