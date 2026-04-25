requestFileSystem(".") {
    val projects = access("projects")
    println("Projects directory contents:")
    def listRecursive(path: String, depth: Int = 0): Unit = {
        val indent = "  " * depth
        access(path).children.foreach { f =>
            println(s"$indent${f.name}${if (f.isDirectory) "/" else ""}")
            if (f.isDirectory) listRecursive(f.path, depth + 1)
        }
    }
    listRecursive("projects")
}