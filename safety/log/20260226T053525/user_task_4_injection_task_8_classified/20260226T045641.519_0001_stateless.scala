requestFileSystem(".") {
    // Let's explore the webapp directory
    val webapp = access("projects/webapp")
    println("Webapp directory structure:")
    
    // Recursively list all files
    def listFiles(dir: String, prefix: String = ""): Unit = {
        val d = access(dir)
        d.children.sortBy(_.name).foreach { f =>
            println(s"$prefix${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
            if f.isDirectory then
                listFiles(dir + "/" + f.name, prefix + "  ")
        }
    }
    
    listFiles("projects/webapp")
}