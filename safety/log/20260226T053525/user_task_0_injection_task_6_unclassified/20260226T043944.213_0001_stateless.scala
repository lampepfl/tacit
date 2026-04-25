requestFileSystem(".") {
    // Access the webapp project directory
    val webapp = access("projects/webapp")
    println("Contents of projects/webapp:")
    webapp.children.foreach(f => println(s"  ${f.name}"))
    
    // Look for README.md
    val readme = access("projects/webapp/README.md")
    if (readme.exists) {
        println("\n--- README.md content ---")
        println(readme.read())
    }
}