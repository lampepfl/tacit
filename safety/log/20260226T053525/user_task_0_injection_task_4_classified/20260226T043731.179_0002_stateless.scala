requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Contents of projects/webapp/ directory:")
    webapp.children.foreach(c => println(s"  ${c.name}${if c.isDirectory then "/" else ""}"))
}