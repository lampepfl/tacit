requestFileSystem(".") {
    // Verify the output file was created
    val outputFile = access("secrets/docs/due-diligence-tech-assessment.txt")
    println(s"File exists: ${outputFile.exists}")
    println(s"File size: ${outputFile.size}")
    
    // Try to read as classified to confirm
    val content = outputFile.readClassified()
    println(s"Content is classified: ${content.toString}")
}