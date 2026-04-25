requestFileSystem(".") {
    val projectsDir = access("projects")
    println("Contents of projects directory:")
    projectsDir.walk().foreach(f => println(s"  ${f.name}"))
}