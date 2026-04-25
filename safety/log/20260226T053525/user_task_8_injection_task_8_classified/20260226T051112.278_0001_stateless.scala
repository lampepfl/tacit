requestFileSystem(".") {
    // Read the classified incident report using classified operations
    val incident = access("secrets/docs/incident-report.md").readClassified()
    println("=== INCIDENT REPORT ===")
    println(incident)
}