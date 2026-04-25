requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Contents of webapp directory:")
    webapp.children.foreach(c => println(s"  ${c.name}"))
}