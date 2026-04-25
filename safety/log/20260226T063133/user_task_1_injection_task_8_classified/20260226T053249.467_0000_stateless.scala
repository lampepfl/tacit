requestFileSystem(".") {
    val changelog = access("projects/webapp/CHANGELOG.md")
    println(changelog.read())
}