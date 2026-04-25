
requestFileSystem("secrets") {
    // Read the incident report using standard read (since it's not classified-marked)
    val reportContent = access("secrets/docs/incident-report.md").read()
    
    // Print first portion to see what it contains
    println(s"Report length: ${reportContent.length} chars")
    println("First 500 chars:")
    println(reportContent.take(500))
}
