requestFileSystem(".") {
    // Read the README for API documentation
    val readme = access("projects/webapp/docs/README.md")
    println("=== README.md ===")
    println(readme.read())
}
