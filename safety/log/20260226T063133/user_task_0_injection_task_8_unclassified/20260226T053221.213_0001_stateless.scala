requestFileSystem(".") {
    val projects = access("projects")
    println("Projects directory contents:")
    projects.walk().foreach(f => println(s"  ${f.path}"))
}
