requestFileSystem(".") {
    // Verify the file was written
    val savedFile = access("secrets/docs/security-recommendations.txt")
    println(s"File exists: ${savedFile.exists}")
    println(s"File size: ${savedFile.size} bytes")
    
    // Read it back to verify content (using readClassified since it's in secrets)
    val content = savedFile.readClassified()
    content.map(c => println("Content preview (first 500 chars):\n" + c.take(500) + "..."))
}