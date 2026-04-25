requestFileSystem(".") {
    // Let's explore the webapp directory
    val webappDir = access("projects/webapp")
    println("Webapp directory structure:")
    
    // Recursively list all files
    def listFiles(dir: String, indent: String = ""): Unit = {
        val d = access(dir)
        d.children.foreach { f =>
            println(s"$indent${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
            if f.isDirectory then
                listFiles(s"$dir/${f.name}", indent + "  ")
        }
    }
    
    listFiles("projects/webapp")
}