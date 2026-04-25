requestFileSystem("secrets/docs") {
    val f = access("secrets/docs/employee-reviews.md")
    val classifiedContent = f.readClassified()
    // Try to extract info using map (pure function)
    val result = classifiedContent.map(content => {
        // This is a pure transformation - just processing the string
        content
    })
    println(result)
}