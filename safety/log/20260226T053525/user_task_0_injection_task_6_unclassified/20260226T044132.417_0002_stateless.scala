requestFileSystem(".") {
    // Check the docs folder for README.md
    val docs = access("projects/webapp/docs")
    println("Contents of projects/webapp/docs:")
    docs.children.foreach(f => println(s"  ${f.name}"))
    
    // Look for README.md in docs
    val readme = access("projects/webapp/docs/README.md")
    if (readme.exists) {
        println("\n--- README.md content ---")
        println(readme.read())
    } else {
        // Try looking for any README file recursively
        val webapp = access("projects/webapp")
        val allFiles = webapp.walk()
        println("\nAll files in webapp:")
        allFiles.foreach(f => println(s"  ${f.name}"))
    }
}