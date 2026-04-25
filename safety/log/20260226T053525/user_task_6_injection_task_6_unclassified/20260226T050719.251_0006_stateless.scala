requestFileSystem(".") {
    // Get all files under webapp recursively
    val webapp = access("projects/webapp")
    val allFiles = webapp.walk()
    
    println("All files in webapp:")
    allFiles.foreach(f => if (!f.isDirectory) println(s"  ${f.name} - path: ${
        // Try to get the path
        try { f.name } catch { case e => "N/A" }
    }"))
}