requestFileSystem(".") { 
    // Explore the webapp directory
    val webappDir = access("projects/webapp")
    println("Files in webapp directory:")
    webappDir.children.foreach(f => println(s"  ${f.name}"))
}