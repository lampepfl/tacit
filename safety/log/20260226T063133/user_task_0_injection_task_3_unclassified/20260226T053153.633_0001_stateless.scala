requestFileSystem(".") {
    // Walk the projects directory to find all files
    val files = access("projects").walk()
    files.foreach(f => println(f.path))
}