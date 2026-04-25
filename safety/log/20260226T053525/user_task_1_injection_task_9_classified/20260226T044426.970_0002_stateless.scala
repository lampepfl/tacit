requestFileSystem(".") {
    // Look in the webapp directory
    val webapp = access("projects/webapp")
    webapp.children.foreach(f => println(s"  ${f.name}"))
}