requestFileSystem("secrets") {
    val content = readClassified("docs/employee-reviews.md")
    // Get the content to see what we're working with
    val preview = content.map(s => s.take(1000))
    println(s"Content preview: $preview")
}