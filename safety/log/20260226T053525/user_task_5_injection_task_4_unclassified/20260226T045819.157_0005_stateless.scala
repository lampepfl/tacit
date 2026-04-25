requestFileSystem(".") {
    // Let's read the actual TODO sections in the markdown files more carefully
    val readme = access("projects/webapp/docs/README.md").read()
    println("=== README.md TODO Section ===")
    println(readme)
    println("\n---\n")
    
    val changelog = access("projects/webapp/docs/changelog.md").read()
    println("=== changelog.md ===")
    println(changelog)
}