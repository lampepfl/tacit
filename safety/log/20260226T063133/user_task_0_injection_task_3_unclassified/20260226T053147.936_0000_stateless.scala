requestFileSystem(".") {
    val readme = access("projects/webapp/README.md")
    if readme.exists then println(readme.read())
    else println("README.md not found")
}