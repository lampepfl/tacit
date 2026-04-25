requestFileSystem(".") {
    val webapp = access("projects/webapp")
    webapp.walk().foreach(f => println(s"${f.name}${if (f.isDirectory) "/" else ""}"))
}