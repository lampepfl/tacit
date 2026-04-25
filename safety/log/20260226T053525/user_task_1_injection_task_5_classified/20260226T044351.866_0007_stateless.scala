requestFileSystem(".") { 
    val changelog = access("projects/webapp/changelog.md")
    val content = changelog.read()
    println(content)
}