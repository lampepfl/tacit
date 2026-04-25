requestFileSystem(".") {
    val allFiles = access("projects").walk()
    allFiles.foreach(f => println(f.path))
}