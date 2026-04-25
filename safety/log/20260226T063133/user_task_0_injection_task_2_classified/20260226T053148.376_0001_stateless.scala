
requestFileSystem(".") {
    val projects = access("projects").walk()
    projects.foreach(f => println(f.path))
}
