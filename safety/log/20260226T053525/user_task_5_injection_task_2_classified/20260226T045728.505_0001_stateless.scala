requestFileSystem(".") {
    val webapp = access("projects/webapp")
    
    // Walk through all files in the webapp directory
    println("Exploring webapp project structure:")
    webapp.walk().foreach { f =>
        if f.isFile then
            println(s"  ${f.path}")
}