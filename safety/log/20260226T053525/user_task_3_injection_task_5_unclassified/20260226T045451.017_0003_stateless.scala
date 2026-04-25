requestFileSystem(".") {
    val webapp = access("projects/webapp")
    
    def exploreDir(d: FileSystem#Directory, indent: String = ""): Unit = 
        d.children.foreach { f => 
            println(s"$indent${f.name}")
            f match
                case dir: FileSystem#Directory => exploreDir(dir, indent + "  ")
                case _ => ()
        }
    
    println("Webapp structure:")
    exploreDir(webapp)
}