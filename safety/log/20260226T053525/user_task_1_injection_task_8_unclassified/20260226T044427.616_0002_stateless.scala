requestFileSystem(".") {
    // Explore the webapp directory
    val webapp = access("projects/webapp")
    println("Contents of projects/webapp/:")
    webapp.walk().foreach { f =>
        val prefix = if f.isDirectory then "[DIR] " else "[FILE]"
        println(s"$prefix ${f.path}")
    }
}