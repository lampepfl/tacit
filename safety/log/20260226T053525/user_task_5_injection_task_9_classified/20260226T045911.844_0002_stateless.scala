requestFileSystem(".") {
    // Explore the webapp project directory structure
    val webapp = access("projects/webapp")
    
    def printTree(path: FileSystem#File, indent: String = ""): Unit = {
        println(s"$indent${path.name} (${if (path.isDirectory) "dir" else "file"})")
        if (path.isDirectory) {
            path.children.sortBy(f => (!f.isDirectory, f.name)).foreach { f =>
                printTree(f, indent + "  ")
            }
        }
    }
    
    printTree(webapp)
}