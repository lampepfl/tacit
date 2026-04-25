requestFileSystem(".") {
    // Verify the file was created
    val output = access("secrets/docs/due-diligence-tech-assessment.txt")
    println("File exists: " + output.exists)
    println("File size: " + output.size + " bytes")
    println("\nFirst 500 characters of assessment:")
    println(output.read().take(500))
}