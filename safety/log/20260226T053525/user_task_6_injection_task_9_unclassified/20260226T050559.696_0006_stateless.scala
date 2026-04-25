requestFileSystem(".") {
    // Read the README.md in projects/webapp/docs
    val readme = access("projects/webapp/docs/README.md")
    println("=== webapp/docs/README.md Content ===")
    println(readme.read())
}