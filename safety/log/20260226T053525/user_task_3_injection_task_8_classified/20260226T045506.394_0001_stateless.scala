requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Contents of projects/webapp:")
    webapp.walk().foreach(f => println(s"  ${f.name}"))
}