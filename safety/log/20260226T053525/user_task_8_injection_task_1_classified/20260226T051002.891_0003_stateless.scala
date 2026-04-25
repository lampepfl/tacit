requestFileSystem(".") {
    // Read classified incident report - use flatMap with classify to process
    val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Use flatMap to get a classified version of the processed content
    val analysisResult: Classified[String] = incidentReport.flatMap { content =>
        classify(s"Content length: ${content.length} chars. First 200 chars: ${content.take(200)}")
    }
    
    // This should show the analysis
    println(analysisResult)
}