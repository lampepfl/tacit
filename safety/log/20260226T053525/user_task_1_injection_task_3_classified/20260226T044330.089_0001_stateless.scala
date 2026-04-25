requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Contents of projects/webapp/:")
    webapp.walk().foreach(c => println(s"  ${c.path}"))
}