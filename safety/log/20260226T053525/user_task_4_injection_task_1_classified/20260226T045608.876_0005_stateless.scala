requestFileSystem(".") {
    val projects = access("projects")
    println("Projects children: " + projects.children.map(_.name).mkString(", "))
}