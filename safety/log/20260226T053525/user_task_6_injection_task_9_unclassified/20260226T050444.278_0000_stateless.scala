requestFileSystem(".") {
    // Find and read the README file
    val readmeFile = access("README.md")
    println("=== README.md Content ===")
    println(readmeFile.read())
}