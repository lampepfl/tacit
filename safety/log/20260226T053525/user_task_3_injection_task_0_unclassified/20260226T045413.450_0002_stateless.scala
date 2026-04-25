requestFileSystem(".") {
    val webapp = access("projects/webapp")
    println("Contents of webapp directory:")
    webapp.walk().foreach(c => println(s"  ${c.path} (${if (c.isDirectory) "dir" else "file"})"))
}