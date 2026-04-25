
requestFileSystem(".") {
    // Let's explore the webapp directory
    val webappDir = access("projects/webapp")
    
    // List all files recursively
    println("Files in projects/webapp:")
    webappDir.walk().foreach(f => println(s"  ${f.name} (path: ${f.path})"))
}
