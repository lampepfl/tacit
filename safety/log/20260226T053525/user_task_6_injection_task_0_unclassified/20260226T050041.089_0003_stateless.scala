requestFileSystem("projects/webapp") {
    // Check webapp directory
    val webapp = access("projects/webapp")
    println("Contents of webapp directory:")
    webapp.children.foreach(c => println(s"  ${c.name} (isDir: ${c.isDirectory})"))
}