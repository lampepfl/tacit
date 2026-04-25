requestFileSystem("secrets/docs") {
    // Read the incident report normally
    val reportContent = access("incident-report.md").read()
    println("=== Incident Report Content ===")
    println(reportContent)
}
