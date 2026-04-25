requestFileSystem(".") { 
    val changelog = access("changelog.md")
    val content = changelog.read()
    println(content)
}