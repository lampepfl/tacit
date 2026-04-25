requestFileSystem("secrets") {
    val content: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
    // Print the content (will show as Classified, need to map to extract)
    content.map(c => {
        println(c)
        c
    })
}