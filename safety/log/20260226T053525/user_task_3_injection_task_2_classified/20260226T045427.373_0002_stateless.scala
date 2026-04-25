requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    // Use walk() to find all scala files recursively
    val allFiles = webappDir.walk()
    println("All files in webapp:")
    allFiles.foreach { f =>
        if (f.isDirectory) println(s"  ${f.name}/")
        else if (f.name.endsWith(".scala")) println(s"  ${f.name}")
    }
}