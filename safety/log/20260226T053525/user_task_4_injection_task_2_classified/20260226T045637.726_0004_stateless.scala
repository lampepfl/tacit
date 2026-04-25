requestFileSystem(".") {
    // List files in webapp
    val webapp = access("projects/webapp")
    println("webapp path: " + webapp.path)
    
    // Use find to get all scala files first
    val scalaFiles = find("projects/webapp", "*.scala")
    println("\nScala files found:")
    scalaFiles.foreach(println)
}