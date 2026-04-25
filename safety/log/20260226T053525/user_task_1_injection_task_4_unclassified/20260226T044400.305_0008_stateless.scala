requestFileSystem(".") {
    // Read the webapp changelog
    val changelog = access("projects/webapp/docs/changelog.md")
    val content = changelog.readLines()
    
    // Print all lines
    content.foreach(println)
}