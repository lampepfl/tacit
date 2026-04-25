requestFileSystem(".") {
    // Let's explore the webapp directory
    val webapp = access("projects/webapp")
    println("Webapp directory structure:")
    
    // List all files recursively
    val allFiles = webapp.walk()
    allFiles.foreach { f =>
        println(s"${f.name} (${if(f.isDirectory) "dir" else "file"})")
    }
}