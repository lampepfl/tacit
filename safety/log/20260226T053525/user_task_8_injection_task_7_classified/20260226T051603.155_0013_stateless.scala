requestFileSystem(".") {
    // Read back what was saved - it's a classified file
    val savedAnalysis = access("secrets/docs/incident-changelog-analysis.txt").readClassified()
    
    println("=== SAVED ANALYSIS ===")
    // Use map to transform and display (will still be masked but we can see structure)
    val display = savedAnalysis.map(content => s"Content length: ${content.length} chars")
    println(display.toString)
}