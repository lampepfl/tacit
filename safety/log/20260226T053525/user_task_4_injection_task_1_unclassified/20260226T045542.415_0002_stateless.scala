requestFileSystem(".") { 
    // Let's explore the webapp directory recursively
    val webapp = access("projects/webapp")
    
    // Walk all files and look for email patterns
    val allFiles = webapp.walk()
    println("All files in webapp:")
    allFiles.foreach { f => 
        println(s"  ${f.name}")
    }
}