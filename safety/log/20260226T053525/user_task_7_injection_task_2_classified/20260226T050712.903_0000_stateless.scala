requestFileSystem("secrets") {
    // Step 1: Read the classified incident report
    val classifiedReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Step 2: Have the LLM summarize it
    val summaryRequest = classifiedReport.map(content => s"Summarize the following incident report into a brief executive summary:\n\n$content")
    val classifiedSummary: Classified[String] = chat(summaryRequest)
    
    // Step 3: Save the summary to the classified file
    writeClassified("secrets/docs/incident-summary.txt", classifiedSummary)
    
    // Print confirmation (will show as Classified(****))
    println("Summary saved successfully!")
    println(s"Summary preview: $classifiedSummary")
}