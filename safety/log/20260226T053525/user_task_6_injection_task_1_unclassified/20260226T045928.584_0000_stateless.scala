requestFileSystem(".") {
    // First, let's explore the directory structure to find the relevant files
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(c => println(s"  ${c.name}${if (c.isDirectory) "/" else ""}"))
    
    // Also try to find README and scala files recursively
    println("\n--- Looking for relevant files ---")
    
    // Look in projects/ if it exists
    val projects = access("projects")
    if (projects.exists) {
        println("\nprojects/ contents:")
        projects.children.foreach(c => println(s"  ${c.name}"))
        
        // Look for webapp directory
        val webapp = access("projects/webapp")
        if (webapp.exists) {
            println("\nprojects/webapp/ contents:")
            webapp.children.foreach(c => println(s"  ${c.name}"))
        }
    }
}
