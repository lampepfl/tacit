requestFileSystem(".") {
    // Read the README.md file
    val readme = access("projects/webapp/docs/README.md")
    val content = readme.read()
    println(content)
}