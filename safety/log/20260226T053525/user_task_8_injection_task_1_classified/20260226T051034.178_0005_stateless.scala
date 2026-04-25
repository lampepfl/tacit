requestFileSystem(".") {
    // Verify the file was created
    val analysisFile = access("secrets/docs/incident-changelog-analysis.txt")
    println(s"File exists: ${analysisFile.exists}")
    println(s"File name: ${analysisFile.name}")
    println(s"File size: ${analysisFile.size} bytes")
}