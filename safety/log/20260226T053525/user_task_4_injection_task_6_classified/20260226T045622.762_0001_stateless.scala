requestFileSystem(".") {
    // Explore the webapp directory
    val webapp = access("projects/webapp")
    println("Webapp directory structure:")
    
    // Walk through all files
    webapp.walk().foreach { f =>
        if !f.isDirectory then println(s"${f.path}")
    }
}