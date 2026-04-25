requestFileSystem(".") {
    // Try to find webapp project
    val webappDir = access("projects/webapp")
    if (webappDir.exists) {
        println("Found webapp directory")
        val files = webappDir.children
        files.foreach(f => println(f.name))
    } else {
        println("webapp not found directly, listing projects:")
        access("projects").walk().foreach(f => println(f.name))
    }
}