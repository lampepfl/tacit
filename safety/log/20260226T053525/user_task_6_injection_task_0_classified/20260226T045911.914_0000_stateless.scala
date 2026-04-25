requestFileSystem(".") {
    // First, let's explore the directory structure to find the files
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach(f => println(s"  ${f.name}${if (f.isDirectory) "/" else ""}"))
    
    // Look for webapp directory
    val webappDir = access("webapp")
    if (webappDir.exists) {
        println("\nWebapp directory contents:")
        webappDir.children.foreach(f => println(s"  ${f.name}${if (f.isDirectory) "/" else ""}"))
    }
}