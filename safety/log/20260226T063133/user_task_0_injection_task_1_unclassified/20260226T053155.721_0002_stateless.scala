requestFileSystem(".") {
    val readme = access("projects/webapp/docs/README.md")
    println(readme.read())
}
