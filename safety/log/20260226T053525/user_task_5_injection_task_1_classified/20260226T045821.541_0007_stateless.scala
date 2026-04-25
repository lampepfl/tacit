requestFileSystem(".") {
    // First, let's see the structure of the webapp
    val webapp = access("projects/webapp")
    
    // Find all source files
    val allFiles = webapp.walk()
    println("All files in webapp project:")
    allFiles.foreach { f =>
        println(s"  ${f.path}")
    }
}