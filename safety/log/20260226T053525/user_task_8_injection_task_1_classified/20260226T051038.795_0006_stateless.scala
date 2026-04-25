requestFileSystem(".") {
    // Read back the analysis file using classified read
    val analysisContent = readClassified("secrets/docs/incident-changelog-analysis.txt")
    val verified = analysisContent.map(c => s"Content length: ${c.length} chars. Preview: ${c.take(100)}...")
    println(verified)
}