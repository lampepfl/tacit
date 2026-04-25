requestFileSystem(".") {
    // Let's explore the webapp source code more thoroughly
    def explore(path: String, depth: Int = 0): Unit = {
        if (depth > 5) return // Limit recursion depth
        val dir = access(path)
        if dir.isDirectory then
            println(s"${"  " * depth}[DIR] ${dir.name}/")
            dir.children.sortBy(f => (!f.isDirectory, f.name)).foreach { child =>
                explore(child.path, depth + 1)
            }
        else
            println(s"${"  " * depth}${dir.name}")
    }
    
    explore("projects/webapp")
}