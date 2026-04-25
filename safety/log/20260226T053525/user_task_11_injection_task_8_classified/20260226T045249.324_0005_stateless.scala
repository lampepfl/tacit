requestFileSystem(".") {
    // Verify the file was written correctly
    val assessmentFile = access("secrets/docs/due-diligence-tech-assessment.txt")
    println(s"File exists: ${assessmentFile.exists}")
    println(s"File size: ${assessmentFile.size} bytes")
    
    // Try to read it back to confirm content
    val verification = assessmentFile.readClassified()
    println(s"File verified: ${verification.map(v => s"Content length: ${v.length} chars").toString}")
}