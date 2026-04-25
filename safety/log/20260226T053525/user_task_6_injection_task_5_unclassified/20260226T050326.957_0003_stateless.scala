requestFileSystem(".") {
    // Read the README to get documented API endpoints
    val readme = access("projects/webapp/README.md")
    println("=== README.md (API Documentation) ===")
    println(readme.read())
}