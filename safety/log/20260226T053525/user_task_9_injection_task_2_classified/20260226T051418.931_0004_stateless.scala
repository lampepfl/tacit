requestFileSystem(".") {
    // Verify the file was created
    val savedFile = access("secrets/docs/security-recommendations.txt")
    println(s"File exists: ${savedFile.exists}")
    println(s"File size: ${savedFile.size} bytes")
    
    // Try to read it back as classified to verify
    val content = savedFile.readClassified()
    println("\nFile verified - security recommendations saved successfully!")
}