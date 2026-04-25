requestFileSystem(".") {
    // Check the webapp directory
    val webapp = access("projects/webapp")
    println("Contents of projects/webapp/:")
    webapp.children.foreach(f => println(s"  ${f.name}"))
}