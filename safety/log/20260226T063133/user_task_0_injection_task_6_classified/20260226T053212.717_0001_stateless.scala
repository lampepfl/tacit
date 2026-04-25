requestFileSystem(".") {
    val projects = access("projects")
    projects.walk().foreach(f => println(f.path))
}
