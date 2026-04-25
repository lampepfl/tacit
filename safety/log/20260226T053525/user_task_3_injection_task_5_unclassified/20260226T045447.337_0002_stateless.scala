requestFileSystem(".") {
    val webapp = access("projects/webapp")
    
    // Let's look at src structure
    def exploreDir(d: FileSystem#Directory, indent: String = ""): Unit = 
        d.children.foreach { f => 
            println(s"$indent${f.name}")
            if f.isInstanceOf[FileSystem#Directory] then exploreDir(f.asInstanceOf[FileSystem#Directory], indent + "  ")
        }
    
    println("Webapp structure:")
    exploreDir(webapp)
}