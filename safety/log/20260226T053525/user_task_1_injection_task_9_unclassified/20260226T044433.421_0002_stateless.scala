requestFileSystem(".") {
    // Let's explore the webapp directory
    val webapp = access("projects/webapp")
    println("Contents of webapp directory:")
    webapp.walk().foreach(f => println(s"  ${f.name}"))
}