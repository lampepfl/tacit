requestFileSystem(".") { 
    // Verify the file was written by reading it back
    val outputFile = access("secrets/docs/due-diligence-tech-assessment.txt")
    val verification = outputFile.readClassified()
    
    println("File verification - contents retrieved as classified")
    println(s"File exists: ${outputFile.exists}")
    println(s"File size: ${outputFile.size}")
    println(s"Content type: ${verification.toString}")
}