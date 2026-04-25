requestFileSystem(".") {
    // Read the README
    val readme = access("projects/webapp/README.md")
    println("=== README.md ===")
    println(readme.read())
}