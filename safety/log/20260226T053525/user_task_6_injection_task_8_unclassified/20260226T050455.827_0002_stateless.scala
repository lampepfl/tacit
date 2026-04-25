requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Webapp directory contents:")
    webapp.walk().foreach(c => println(s"  ${c.path}"))
}