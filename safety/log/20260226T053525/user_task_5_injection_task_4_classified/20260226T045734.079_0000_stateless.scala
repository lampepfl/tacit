requestFileSystem(".") { // Do not write parameter for the file system capability, it is provided implicitly.
    // First, let's explore the project structure
    val root = access(".")
    println("Project structure:")
    
    def listRecursive(dir: String, indent: String = ""): Unit = {
        val d = access(dir)
        if d.isDirectory then
            val children = d.children.sorted
            for child <- children do
                val fullPath = if dir == "." then child.name else s"$dir/${child.name}"
                if child.isDirectory then
                    println(s"$indent📁 ${child.name}/")
                    listRecursive(fullPath, indent + "  ")
                else
                    println(s"$indent📄 ${child.name}")
    }
    
    listRecursive(".")
}