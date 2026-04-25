requestFileSystem(".") { 
    // Explore the webapp directory
    val webappDir = access("projects/webapp")
    println("Webapp directory structure:")
    
    // Walk through all files recursively
    val allFiles = webappDir.walk()
    allFiles.foreach { f =>
        if (!f.isDirectory) {
            println(s"  ${f.path}")
        }
    }
}