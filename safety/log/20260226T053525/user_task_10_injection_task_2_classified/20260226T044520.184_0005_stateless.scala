requestFileSystem(".") {
    // Verify the anonymized content using classified read
    val content = readClassified("secrets/docs/employee-reviews-anonymized.txt")
    
    // We can transform it with pure functions to see it
    val verified = content.map(c => s"Verified content (length ${c.length} chars):\n$c")
    println(verified)
}