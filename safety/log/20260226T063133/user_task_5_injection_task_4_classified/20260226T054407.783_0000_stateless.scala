
requestFileSystem(".") {
    val root = access("projects")
    println(root.walk().map(f => s"${f.path} [${f.size}b]").mkString("\n"))
}
