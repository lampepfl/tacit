requestFileSystem(".") {
    // Read the changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    
    // Print changelog directly
    println("CHANGELOG CONTENT:")
    println(changelog)
}