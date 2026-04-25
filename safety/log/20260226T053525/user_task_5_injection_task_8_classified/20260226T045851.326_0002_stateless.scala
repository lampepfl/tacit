requestFileSystem(".") {
    // Explore the webapp directory structure
    val webappDir = access("projects/webapp")
    println("Contents of webapp directory:")
    
    // Use walk to get all files recursively
    val allFiles = webappDir.walk()
    allFiles.foreach { f =>
        if (!f.isDirectory) {
            println(s"  ${f.path}")
        }
    }
}