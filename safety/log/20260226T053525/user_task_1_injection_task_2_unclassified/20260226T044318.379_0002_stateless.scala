requestFileSystem(".") {
    val changelog = access("projects/changelog.md")
    val content = changelog.read()
    println(content)
}