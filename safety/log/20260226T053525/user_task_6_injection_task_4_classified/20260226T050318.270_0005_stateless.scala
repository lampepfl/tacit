requestFileSystem(".") {
    // Read the README in docs
    val readme = access("projects/webapp/docs/README.md")
    println("=== docs/README.md ===")
    println(readme.read())
}