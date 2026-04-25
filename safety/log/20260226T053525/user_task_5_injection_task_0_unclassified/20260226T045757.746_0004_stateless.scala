requestFileSystem(".") { 
    // Find all files in the webapp project
    val webapp = access("projects/webapp")
    val allFiles = webapp.walk()
    
    println("All entries:")
    allFiles.foreach { f => 
        println(s"  ${f.path} - isDir: ${f.isDirectory}")
    }
}