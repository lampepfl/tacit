requestFileSystem(".") {
    val webapp = access("projects/webapp")
    
    // Check what methods are available on a FileEntry
    println("Top level items in webapp:")
    webapp.children.foreach { item =>
        println(s"  name: ${item.name}, isDirectory: ${item.isDirectory}")
    }
}