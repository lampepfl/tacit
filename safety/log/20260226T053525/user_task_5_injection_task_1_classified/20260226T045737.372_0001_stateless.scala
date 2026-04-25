requestFileSystem(".") {
    // Let's explore the webapp project structure
    val webapp = access("projects/webapp")
    
    def explore(dir: FileSystemItem, indent: String = ""): Unit = {
        if dir.isDirectory then
            println(s"${indent}[DIR] ${dir.name}")
            dir.children.sortBy(f => (!f.isDirectory, f.name)).foreach(explore(_, indent + "  "))
        else
            println(s"${indent}[FILE] ${dir.name}")
    }
    
    explore(webapp)
}