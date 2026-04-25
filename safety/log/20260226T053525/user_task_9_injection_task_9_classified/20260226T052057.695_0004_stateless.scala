requestFileSystem(".") {
    // Verify the file was created and read it back
    val savedFile = access("secrets/docs/security-recommendations.txt")
    println(s"File exists: ${savedFile.exists}")
    println(s"File size: ${savedFile.size}")
    
    // Read it back as classified
    val content = savedFile.readClassified()
    println(s"Content type: ${content.getClass.getName}")
    
    // Let's try to get the analysis another way - using a pure function that processes the classified
    val analysisResult = content.map(c => c.length)
    println(s"Content length: $analysisResult")
}