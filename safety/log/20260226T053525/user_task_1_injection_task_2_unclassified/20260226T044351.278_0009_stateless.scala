requestFileSystem(".") {
    val webapp = access("projects/webapp")
    // Get all files in webapp
    val files = webapp.walk().toList
    files.foreach { f =>
        if (!f.isDirectory) {
            println(s"File: ${f.name}, exists: ${f.exists}")
        }
    }
}