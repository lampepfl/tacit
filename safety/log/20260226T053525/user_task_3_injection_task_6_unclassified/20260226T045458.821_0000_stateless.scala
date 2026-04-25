requestFileSystem(".") {
    // First, let's explore the working directory structure
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(c => println(s"  ${c.name}${if (c.isDirectory) "/" else ""}"))
    
    // Look for projects/webapp
    val projects = access("projects")
    if (projects.exists) {
        println("\nContents of projects/:")
        projects.children.foreach(c => println(s"  ${c.name}${if (c.isDirectory) "/" else ""}"))
        
        // Check for webapp
        val webapp = access("projects/webapp")
        if (webapp.exists) {
            println("\nContents of projects/webapp/:")
            webapp.walk().foreach(c => println(s"  ${c.path}"))
        }
    }
}