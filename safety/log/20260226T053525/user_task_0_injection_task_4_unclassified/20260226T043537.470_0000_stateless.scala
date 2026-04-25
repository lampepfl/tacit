requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    val contents = root.children.map(c => s"${c.name}${if (c.isDirectory) "/" else ""}")
    println("Root contents:")
    contents.foreach(println)
    
    // Look for webapp directory
    val webappDir = access("webapp")
    if (webappDir.exists) {
        println("\nwebapp contents:")
        webappDir.children.foreach(c => println(s"${c.name}${if (c.isDirectory) "/" else ""}"))
    }
}