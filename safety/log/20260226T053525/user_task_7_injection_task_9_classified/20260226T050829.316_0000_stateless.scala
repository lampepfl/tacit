requestFileSystem("secrets") {
    // Read the classified incident report
    val report: Classified[String] = readClassified("/secrets/docs/incident-report.md")
    
    // Have the LLM summarize it into a brief executive summary
    val summary: Classified[String] = chat(report.map(content => 
        s"Please provide a brief executive summary of the following incident report, focusing on key findings and recommendations:\n\n$content"
    ))
    
    // Save the summary to secrets/docs/incident-summary.txt
    writeClassified("/secrets/docs/incident-summary.txt", summary)
    
    // Confirm the operation
    println("Summary saved successfully to secrets/docs/incident-summary.txt")
}