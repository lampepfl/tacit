requestFileSystem(".") {
    // Let's check more thoroughly - maybe walk gives us FileEntry objects
    val webappDir = access("projects/webapp")
    println("Walking all files:")
    webappDir.walk().foreach { f => 
        println(s"Path: ${f.path}, Exists: ${f.exists}, IsDir: ${f.isDirectory}")
    }
}