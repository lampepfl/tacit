requestFileSystem("projects/webapp") {
    // Get the root of the webapp directory
    val webapp = access(".")
    
    // List all files using walk()
    val allFiles = webapp.walk()
    
    println(s"Found ${allFiles.length} items in webapp")
    
    // Filter to just files
    val files = allFiles.filter(!_.isDirectory)
    println(s"Found ${files.length} files")
    
    // Show file names
    files.foreach(f => println(s"  ${f.path}"))
}