requestFileSystem(".") {
    // Let's explore the projects directory
    val projects = access("projects")
    
    // Walk through all files
    def listFiles(dir: FileAccess, indent: String = ""): Unit = {
        dir.children.foreach { f =>
            if f.isDirectory then
                println(s"${indent}[DIR] ${f.name}")
                listFiles(f, indent + "  ")
            else
                println(s"${indent}[FILE] ${f.name}")
        }
    }
    
    listFiles(projects)
}