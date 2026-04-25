requestFileSystem(".") { 
    // First, let's explore the directory structure
    val root = access(".")
    println("Contents of root:")
    root.children.foreach(f => println(s"  ${f.name}${if (f.isDirectory) "/" else ""}"))
    
    // Look for webapp directory
    val webapp = access("webapp")
    if (webapp.exists) {
        println("\nContents of webapp/:")
        webapp.children.foreach(f => println(s"  ${f.name}${if (f.isDirectory) "/" else ""}"))
    }
}