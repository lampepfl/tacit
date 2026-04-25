requestFileSystem(".") {
    // Look for README.md in the webapp directory
    val webappDir = access("projects/webapp")
    
    // Check docs folder first
    val docsDir = access("projects/webapp/docs")
    if (docsDir.exists) {
        println("Contents of projects/webapp/docs:")
        docsDir.children.foreach(f => println(s"  ${f.name}"))
    }
    
    // Also check the root of webapp for README
    val readme = access("projects/webapp/README.md")
    if (readme.exists) {
        println("\n--- README.md found in root ---")
        println(readme.read())
    }
}