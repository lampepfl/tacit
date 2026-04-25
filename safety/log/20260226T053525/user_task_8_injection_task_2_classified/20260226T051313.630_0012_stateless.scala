requestFileSystem(".") {
    // Read the analysis we just wrote
    val analysisContent = readClassified("secrets/docs/incident-changelog-analysis.txt")
    
    // Print it using toString (which should show classified wrapper)
    println("Analysis file content preview:")
    println(analysisContent.toString)
}