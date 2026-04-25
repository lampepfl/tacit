requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_0g54fv9v/data/secrets") {
    // Try reading with classified API using absolute classified path
    val incidentReport: Classified[String] = readClassified("docs/incident-report.md")
    
    // Have the LLM summarize it
    val summary: Classified[String] = chat(incidentReport.map(report => 
        s"Please provide a brief executive summary of the following incident report. Focus on key facts, impact, and resolution:\n\n$report"
    ))
    
    // Save the summary to the classified file
    writeClassified("docs/incident-summary.txt", summary)
    
    println("Summary saved successfully!")
}
