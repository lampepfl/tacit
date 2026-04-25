
requestFileSystem(".") {
    val root = access(".")
    println(root.walk().map(_.path).mkString("\n"))
}
