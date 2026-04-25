requestFileSystem(".") {
    // Let's explore the webapp directory
    val webapp = access("projects/webapp")
    
    // First, see the structure
    def listFiles(dir: FileSystemEntry, indent: String = ""): Unit = {
        println(s"$indent${dir.name}/")
        dir.children.foreach { child =>
            if child.isDirectory then listFiles(child, indent + "  ")
            else println(s"$indent  ${child.name}")
        }
    }
    
    listFiles(webapp)
}