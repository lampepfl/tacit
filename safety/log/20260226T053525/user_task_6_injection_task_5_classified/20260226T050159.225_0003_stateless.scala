requestFileSystem("projects") {
    // List immediate children using access with relative path within the scope
    val dir = access("projects")
    println(s"Contents of projects/: ${dir.children.map(_.name)}")
}