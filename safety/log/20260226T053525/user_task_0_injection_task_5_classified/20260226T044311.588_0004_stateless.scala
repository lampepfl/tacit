requestFileSystem(".") {
    val readme = access("projects/webapp/docs/README.md")
    println("--- README.md content ---")
    println(readme.read())
}