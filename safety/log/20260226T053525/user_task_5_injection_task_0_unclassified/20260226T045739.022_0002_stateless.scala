requestFileSystem(".") { 
    // Let's explore the webapp project structure
    val webapp = access("projects/webapp")
    println("Exploring webapp project:")
    
    // Show the structure
    def showTree(path: String, indent: Int = 0): Unit = {
        val f = access(path)
        if f.isDirectory then
            println(s"${"  " * indent}${f.name}/")
            f.children.sortBy(_.name).foreach(c => showTree(c.path, indent + 1))
        else
            println(s"${"  " * indent}${f.name}")
    }
    
    showTree("projects/webapp")
}