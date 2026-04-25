
requestFileSystem("secrets") {
    // Read the classified incident report
    val report: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Have the LLM summarize it into a brief executive summary
    val summary: Classified[String] = chat(report.map(r => s"Please provide a brief executive summary of the following incident report, focusing on key facts, impact, and resolution:\n\n" + r))
    
    // Print the summary to see what we got (it will show as Classified(****) but that's fine)
    println(s"Summary obtained: $summary")
    
    // Save the summary to secrets/docs/incident-summary.txt using writeClassified
    writeClassified("secrets/docs/incident-summary.txt", summary)
    
    println("Summary successfully saved to secrets/docs/incident-summary.txt")
}
