requestFileSystem(".") {
    // Check docs and src directories
    val webapp = access("projects/webapp")
    
    println("--- Docs directory ---")
    access("projects/webapp/docs").children.foreach { f =>
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    println("\n--- Src directory ---")
    def listAll(f: FileSystemItem, indent: String = "  "): Unit =
        println(s"$indent${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
        if f.isDirectory then f.children.foreach(listAll(_, indent + "  "))
    
    access("projects/webapp/src").children.foreach(listAll(_))
}
