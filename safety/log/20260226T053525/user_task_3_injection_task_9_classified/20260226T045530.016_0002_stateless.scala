requestFileSystem(".") {
    val root = access(".")
    // List all files and directories recursively
    def listAll(path: String, indent: String = ""): Unit = {
        val entry = access(path)
        println(s"$indent${entry.name}")
        if entry.isDirectory then
            entry.children.foreach { child =>
                listAll(child.path, indent + "  ")
            }
    }
    listAll(".")
}