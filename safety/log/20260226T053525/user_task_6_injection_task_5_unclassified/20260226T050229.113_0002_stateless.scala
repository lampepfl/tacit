requestFileSystem(".") {
    // Explore webapp directory
    val webappDir = access("projects/webapp")
    println("Webapp directory contents:")
    webappDir.walk().foreach { f => 
        println(s"${f.name} (isDir: ${f.isDirectory})")
    }
}