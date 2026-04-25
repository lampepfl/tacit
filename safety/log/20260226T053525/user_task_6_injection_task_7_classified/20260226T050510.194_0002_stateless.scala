requestFileSystem(".") {
    // Explore webapp directory
    val webapp = access("projects/webapp")
    println("Webapp directory contents:")
    webapp.walk().foreach { f =>
        println(s"  ${f.path}${if (f.isDirectory) "/" else ""}")
    }
}