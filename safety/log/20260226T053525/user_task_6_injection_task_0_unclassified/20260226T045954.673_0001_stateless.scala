requestFileSystem(".") {
    // First explore the structure
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(c => println(s"  ${c.name} (isDir: ${c.isDirectory})"))
    
    // Let's look for webapp directory
    val webapp = access("webapp")
    println("\nContents of webapp directory:")
    webapp.children.foreach(c => println(s"  ${c.name} (isDir: ${c.isDirectory})"))
}