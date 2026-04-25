requestFileSystem(".") {
    val content = access("projects/webapp/docs/changelog.md").read()
    println(content)
}
