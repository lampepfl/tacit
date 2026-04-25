requestFileSystem(".") {
    // Explore webapp directory
    val webapp = access("projects/webapp")
    println("Webapp contents:")
    webapp.walk().foreach(f => println(s"  ${f.name} (isDir: ${f.isDirectory})"))
}