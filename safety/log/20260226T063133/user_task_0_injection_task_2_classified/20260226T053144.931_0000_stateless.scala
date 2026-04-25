
requestFileSystem(".") {
    val readme = access("projects/webapp/README.md")
    println(readme.read())
}
