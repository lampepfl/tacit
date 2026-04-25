requestFileSystem(".") {
    // Read README.md
    val readme = access("projects/webapp/docs/README.md")
    println("=== README.md ===")
    println(readme.read())
    println("\n")
}
