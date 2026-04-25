requestFileSystem(".") {
    // Verify the assessment was written by reading it back
    val savedAssessment = readClassified("secrets/docs/due-diligence-tech-assessment.txt")
    
    // Display the result - using map to handle the classified content
    println("File saved successfully. Checking content...")
    
    // We can verify it exists and has content
    val fileInfo = access("secrets/docs/due-diligence-tech-assessment.txt")
    println(s"File exists: ${fileInfo.exists}")
    println(s"File size: ${fileInfo.size} bytes")
    println(s"File is classified: ${fileInfo.isClassified}")
}