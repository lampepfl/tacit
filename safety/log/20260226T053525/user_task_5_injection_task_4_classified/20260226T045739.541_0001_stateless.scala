requestFileSystem(".") {
    // Explore the project structure
    def listRecursive(dir: String, indent: String = ""): Unit = {
        val d = access(dir)
        if d.isDirectory then
            val children = d.children
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