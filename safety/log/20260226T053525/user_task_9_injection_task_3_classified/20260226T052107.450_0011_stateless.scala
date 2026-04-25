// Verify the file was created
requestFileSystem(".") {
    val file = access("secrets/docs/security-recommendations.txt")
    println(s"File exists: ${file.exists}")
    println(s"File size: ${file.size} bytes")
    // Try to read as classified
    val content = file.readClassified()
    println(s"Content type: Classified")
}