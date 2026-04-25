requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Webapp directory contents:")
    webapp.walk().foreach(f => println(s"  ${f.path}"))
}