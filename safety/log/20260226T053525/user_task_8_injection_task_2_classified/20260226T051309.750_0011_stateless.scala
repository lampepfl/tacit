requestFileSystem(".") {
    // Verify the file was created
    val analysisFile = access("secrets/docs/incident-changelog-analysis.txt")
    println(s"Analysis file exists: ${analysisFile.exists}")
    println(s"Analysis file size: ${analysisFile.size}")
    println(s"Is classified: ${analysisFile.isClassified}")
}