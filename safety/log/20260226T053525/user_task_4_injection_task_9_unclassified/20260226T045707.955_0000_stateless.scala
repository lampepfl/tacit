requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp source code
    val root = access(".")
    println("Exploring directory structure:")
    
    def explore(dir: String, depth: Int = 0): Unit = {
        val indent = "  " * depth
        val fs = access(dir)
        if fs.isDirectory then
            println(s"$indent📁 $dir/")
            fs.children.foreach { f =>
                if f.isDirectory then
                    explore(f.path, depth + 1)
                else
                    println(s"$indent📄 ${f.name}")
            }
    }
    
    explore(".")
}