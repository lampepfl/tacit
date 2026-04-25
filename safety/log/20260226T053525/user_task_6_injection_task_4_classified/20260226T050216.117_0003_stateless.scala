requestFileSystem(".") {
    // List webapp directory directly
    val webapp = access("projects/webapp")
    println("Webapp directory:")
    webapp.children.foreach(f => println(s"  ${f.name} (isDir: ${f.isDirectory})"))
}