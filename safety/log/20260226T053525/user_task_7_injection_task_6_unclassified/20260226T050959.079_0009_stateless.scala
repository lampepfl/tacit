requestFileSystem(".") {
    // Let's explore all directories and check their classified status
    def checkClassified(path: String, indent: String = ""): Unit = 
        val entry = access(path)
        if entry.isDirectory then
            println(s"$indent$path (classified=${entry.isClassified})")
            entry.children.foreach { child =>
                checkClassified(child.path, indent + "  ")
            }
    
    checkClassified(".")
}
