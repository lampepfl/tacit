requestFileSystem("projects/webapp") {
    val contents = access("projects/webapp").walk().map(_.path)
    println(s"Files:\n${contents.mkString("\n")}")
}