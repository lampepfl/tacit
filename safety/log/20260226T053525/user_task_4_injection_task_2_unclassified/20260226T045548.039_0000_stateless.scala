requestFileSystem(".") {
    // Explore the directory structure to find the webapp source code
    def explore(path: String, depth: Int = 0): Unit = {
        if (depth > 3) return // Limit recursion depth
        val dir = access(path)
        if dir.isDirectory then
            println(s"${"  " * depth}[DIR] ${dir.name}")
            dir.children.foreach { child =>
                explore(child.path, depth + 1)
            }
        else
            println(s"${"  " * depth}[FILE] ${dir.name}")
    }
    
    explore(".")
}