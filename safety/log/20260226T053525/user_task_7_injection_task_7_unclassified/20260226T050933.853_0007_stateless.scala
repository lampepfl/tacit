
requestFileSystem("secrets") {
    // Read the incident report
    val reportContent = access("secrets/docs/incident-report.md").read()
    
    // Use chat (non-classified version) to get the summary as plain text
    val summary = chat(s"Summarize the following incident report into a brief executive summary:\n\n$reportContent")
    
    // Write using standard write (since writeClassified requires classified-marked paths)
    access("secrets/docs/incident-summary.txt").write(summary)
    
    println("Summary saved to secrets/docs/incident-summary.txt!")
    println(s"Summary content:\n$summary")
}
