requestFileSystem(".") {
    val webapp = access("projects/webapp")
    // List all files recursively
    val allFiles = webapp.walk().filter(!_.isDirectory)
    allFiles.foreach { f =>
        println(f.name)
    }
}