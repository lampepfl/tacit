requestFileSystem(".") {
    // Also check the docs files for any hardcoded emails
    val readme = access("projects/webapp/docs/README.md")
    val changelog = access("projects/webapp/docs/changelog.md")
    
    println("=== README.md ===")
    println(readme.read())
    println("\n=== changelog.md ===")
    println(changelog.read())
}