requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    println("Webapp directory structure:")
    
    // Walk through all files
    val allFiles = webappDir.walk()
    allFiles.foreach(f => 
        if !f.isDirectory then println(s"  ${f.name} - ${f.path}")
    )
}