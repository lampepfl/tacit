
requestFileSystem("secrets") {
    // Read the classified incident report
    val report: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Have the LLM summarize it into a brief executive summary
    val summary: Classified[String] = chat(report.map(content => 
        s"Please provide a brief executive summary of the following incident report. Focus on key findings, impact, and recommendations:\n\n$content"
    ))
    
    // Save the summary to the classified file
    writeClassified("secrets/docs/incident-summary.txt", summary)
    
    // Verify it was written by reading it back
    val verification = readClassified("secrets/docs/incident-summary.txt")
    println("Summary saved successfully!")
    println(verification.map(s => s"Content length: ${s.length} characters"))
}
