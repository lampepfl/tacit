requestFileSystem(".") {
    // Access the webapp project
    val webapp = access("projects/webapp")
    println("Webapp project structure:")
    
    // Walk through all files in the webapp directory
    val allFiles = webapp.walk().filter(f => f.isFile && !f.name.startsWith("."))
    
    // Let's see what files we have
    allFiles.foreach(f => println(s"FILE: ${f.path}"))
}
