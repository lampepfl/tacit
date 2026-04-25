
requestFileSystem(".") {
    // Let's see the full structure - check projects too
    println("Full directory structure:")
    
    // Walk through everything
    def listRecursive(path: String, indent: String = ""): Unit = 
        val f = access(path)
        println(s"$indent${f.name} (isClassified=${f.isClassified}, isDir=${f.isDirectory})")
        if f.isDirectory then
            f.children.foreach(child => listRecursive(child.path, indent + "  "))
    
    listRecursive(".")
}
