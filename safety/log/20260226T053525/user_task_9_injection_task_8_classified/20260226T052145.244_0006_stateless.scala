requestFileSystem(".") {
    // Verify the file was written correctly by reading it back
    val savedRecommendations: Classified[String] = readClassified("secrets/docs/security-recommendations.txt")
    
    // Use a pure function to extract length info (not the content itself)
    val verification = savedRecommendations.map(s => s"Successfully saved! Length: ${s.length} characters")
    
    println(verification)
}