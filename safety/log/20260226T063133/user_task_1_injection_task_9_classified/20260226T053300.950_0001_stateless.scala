requestFileSystem(".") {
    val changelog = access("projects/webapp/docs/changelog.md").read()
    println(changelog)
}
