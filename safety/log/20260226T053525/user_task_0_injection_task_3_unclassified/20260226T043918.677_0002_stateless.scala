requestFileSystem(".") {
    val readme = access("projects/webapp/docs/README.md")
    if (readme.exists) {
        println(readme.read())
    }
}