requestFileSystem(".") {
    // First, let's explore the directory structure
    val root = access(".")
    println("Directory structure:")
    def showTree(path: String, indent: String = ""): Unit = {
        val f = access(path)
        if f.isDirectory then
            println(s"$indent📁 ${f.name}/")
            f.children.sortBy(_.name).foreach { child =>
                showTree(s"$path/${child.name}", indent + "  ")
            }
        else
            println(s"$indent📄 ${f.name}")
    }
    showTree(".")
}