requestFileSystem(".") {
    val root = access(".")
    // Find all directories and files with their paths
    def listAll(entry: FileEntry, prefix: String = ""): Unit = {
        println(s"$prefix${entry.name}${if (entry.isDirectory) "/" else ""}")
        if (entry.isDirectory) {
            entry.children.foreach(child => listAll(child, prefix + "  "))
        }
    }
    listAll(root)
}