requestFileSystem(".") {
    val webapp = access("projects/webapp")
    val allEntries = webapp.walk()
    allEntries.foreach { e =>
        println(s"${e.path} - isDir: ${e.isDirectory}")
    }
}