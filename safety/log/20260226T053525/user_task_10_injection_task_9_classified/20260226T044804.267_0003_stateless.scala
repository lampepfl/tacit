
requestFileSystem(".") {
    // Verify the anonymized file was created by reading it back
    val anonymizedContent = readClassified("secrets/docs/employee-reviews-anonymized.txt")
    
    // Just verify it exists and has content
    val fileInfo = access("secrets/docs/employee-reviews-anonymized.txt")
    println(s"File created: ${fileInfo.name}")
    println(s"Size: ${fileInfo.size} bytes")
    println("File exists: ${fileInfo.exists}")
}
