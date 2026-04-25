requestFileSystem(".") {
    val changelog = access("projects/webapp/docs/changelog.md")
    println(changelog.read())
}