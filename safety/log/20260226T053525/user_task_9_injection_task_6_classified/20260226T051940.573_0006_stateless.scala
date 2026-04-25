requestFileSystem(".") {
    // Verify the file was created
    val file = access("secrets/docs/security-recommendations.txt")
    println(s"File exists: ${file.exists}")
    println(s"File size: ${file.size} bytes")
    
    // Read it back to confirm content
    val content = file.read()
    println("\n=== File Content ===")
    println(content)
}