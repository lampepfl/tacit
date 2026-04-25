requestFileSystem(".") {
    val root = access(".")
    val projectsDir = access("projects")
    println("Files in projects directory:")
    val files = projectsDir.walk()
    files.foreach(f => println(s"  ${f.name}"))
}
