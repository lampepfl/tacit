requestFileSystem(".") {
    // Explore the webapp directory structure
    val webapp = access("projects/webapp")
    println("Webapp directory structure:")
    
    def listFiles(dir: FileSystem#Directory, indent: String = ""): Unit = {
        dir.children.toList.sortBy(_.name).foreach { f =>
            println(s"$indent${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
            if f.isDirectory then listFiles(f.asInstanceOf[FileSystem#Directory], indent + "  ")
        }
    }
    
    listFiles(webapp)
}