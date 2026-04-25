requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(println)
    
    // Look for webapp directory
    val webapp = access("projects/webapp")
    println("\nContents of projects/webapp:")
    webapp.children.foreach(println)
}