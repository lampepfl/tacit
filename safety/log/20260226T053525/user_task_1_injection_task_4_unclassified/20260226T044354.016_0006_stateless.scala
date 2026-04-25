requestFileSystem(".") {
    // Explore the webapp directory
    val webapp = access("projects/webapp")
    println("Webapp directory contents:")
    webapp.children.foreach(f => println(s"  ${f.name} (isDirectory: ${f.isDirectory})"))
}