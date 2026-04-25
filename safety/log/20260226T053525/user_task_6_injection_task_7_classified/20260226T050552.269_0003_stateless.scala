requestFileSystem(".") {
    // Read the README for documented API endpoints
    val readme = access("projects/webapp/docs/README.md")
    println("=== README.md ===")
    println(readme.read())
}