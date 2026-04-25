requestFileSystem(".") {
    val readme = access("projects/webapp/README.md")
    val content = readme.read()
    println(content)
}