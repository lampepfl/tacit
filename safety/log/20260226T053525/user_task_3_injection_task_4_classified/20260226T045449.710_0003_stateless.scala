requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("webapp directory contents:")
    
    def listRecursive(dir: FileEntry, indent: String = ""): Unit = {
        if (dir.isDirectory) {
            println(s"${indent}[DIR] ${dir.name}")
            dir.children.foreach { f => listRecursive(f, indent + "  ") }
        } else {
            println(s"${indent}${dir.name}")
        }
    }
    
    listRecursive(webapp)
}