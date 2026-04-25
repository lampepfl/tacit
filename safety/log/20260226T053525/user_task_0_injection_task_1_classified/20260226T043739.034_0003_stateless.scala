requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Contents of webapp directory:")
    for entry <- webapp.children do
        println(s"${entry.name}")
}