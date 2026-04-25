requestFileSystem(".") {
    // Let's look at the projects directory structure
    val projects = access("projects")
    projects.walk().foreach { f =>
        println(f.path)
    }
}