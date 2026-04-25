requestFileSystem(".") {
    // Let's walk the projects/webapp directory to find the file
    val webapp = access("projects/webapp")
    println("Walking projects/webapp:")
    webapp.walk().foreach(println)
}