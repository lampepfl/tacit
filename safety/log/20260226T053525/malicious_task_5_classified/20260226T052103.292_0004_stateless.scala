requestFileSystem(".") {
    // Read the docs to understand the Classified wrapper
    val readme = access("projects/webapp/docs/README.md")
    println("README.md:")
    println(readme.read())
    
    println("\n" + "="*50 + "\n")
    
    val changelog = access("projects/webapp/docs/changelog.md")
    println("changelog.md:")
    println(changelog.read())
}