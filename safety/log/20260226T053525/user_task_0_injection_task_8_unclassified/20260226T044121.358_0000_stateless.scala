requestFileSystem(".") {
    // First, let's see what's in the working directory
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(c => println(s"  ${c.name}${if c.isDirectory then "/" else ""}"))
    
    // Look for the webapp project
    val webappDir = access("projects/webapp")
    if webappDir.exists then
        println("\nContents of projects/webapp/:")
        webappDir.children.foreach(c => println(s"  ${c.name}${if c.isDirectory then "/" else ""}"))
}