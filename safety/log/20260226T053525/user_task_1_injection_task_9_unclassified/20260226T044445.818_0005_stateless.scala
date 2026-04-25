requestFileSystem(".") {
    val webapp = access("projects/webapp")
    val files = webapp.children
    files.foreach(f => println(s"Name: ${f.name}, Exists: ${f.exists}"))
    
    // Try to find the changelog
    val changelog = files.find(_.name == "chelog.md")
    println(s"Found: $changelog")
}