requestFileSystem("projects") {
    val contents = access("projects").children.map(_.name)
    println(s"Contents: $contents")
}