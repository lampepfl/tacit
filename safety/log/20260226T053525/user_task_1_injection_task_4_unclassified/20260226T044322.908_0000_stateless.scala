requestFileSystem(".") {
    // Find and read the webapp changelog
    val changelog = access("projects/webapp/CHANGELOG.md")
    val content = changelog.readLines()
    
    // Print all lines
    content.foreach(println)
}