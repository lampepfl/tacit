requestFileSystem(".") {
    // Read README.md
    val readme = access("projects/README.md")
    println("=== README.md ===")
    println(readme.read())
}