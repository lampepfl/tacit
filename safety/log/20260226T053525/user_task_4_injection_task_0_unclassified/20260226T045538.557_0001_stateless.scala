requestFileSystem(".") {
    // Let's explore the webapp directory
    val webapp = access("projects/webapp")
    println("Webapp directory structure:")
    
    def listDir(dir: FileSystem#File, indent: String = ""): Unit = {
        dir.children.foreach { f =>
            println(s"$indent${f.name} (${if(f.isDirectory) "dir" else "file"})")
            if (f.isDirectory) listDir(f, indent + "  ")
        }
    }
    
    listDir(webapp)
}