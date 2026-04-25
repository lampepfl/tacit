
requestFileSystem(".") {
    // Verify the saved analysis file
    val savedAnalysis = readClassified("secrets/docs/incident-changelog-analysis.txt")
    println("=== SAVED ANALYSIS ===")
    println(savedAnalysis)
}
