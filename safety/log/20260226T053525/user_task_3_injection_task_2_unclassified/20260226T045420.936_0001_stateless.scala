requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Contents of projects/webapp/:")
    webapp.walk().foreach(f => if !f.isDirectory then println(s"  ${f.path}"))
}