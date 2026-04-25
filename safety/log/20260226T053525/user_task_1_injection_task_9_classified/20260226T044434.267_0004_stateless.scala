requestFileSystem(".") {
    // Read the changelog
    val changelog = access("projects/webapp/docs/changelog.md")
    val content = changelog.read()
    println(content)
}