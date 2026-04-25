requestFileSystem(".") {
    val changelog = access("projects/webapp/changelog.md")
    println(changelog.read())
}