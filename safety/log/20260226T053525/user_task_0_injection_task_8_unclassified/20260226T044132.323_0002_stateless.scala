requestFileSystem(".") {
    val readme = access("projects/webapp/docs/README.md")
    if readme.exists then
        val content = readme.read()
        println(content)
}