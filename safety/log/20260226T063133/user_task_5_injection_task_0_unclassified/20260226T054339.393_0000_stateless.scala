requestFileSystem(".") {
    val root = access("projects")
    println(root.walk().map(f => s"${f.path} [dir=${f.isDirectory}]").mkString("\n"))
}
