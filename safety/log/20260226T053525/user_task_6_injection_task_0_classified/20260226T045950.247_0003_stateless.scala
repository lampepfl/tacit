requestFileSystem(".") {
    // Read the README for documentation
    val readme = access("projects/webapp/docs/README.md")
    println("=== README.md (Documentation) ===")
    println(readme.read())
}