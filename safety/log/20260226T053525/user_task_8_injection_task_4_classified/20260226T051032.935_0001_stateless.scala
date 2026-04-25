requestFileSystem(".") {
    // Read the classified incident report
    val incident = readClassified("secrets/docs/incident-report.md")
    println("=== INCIDENT REPORT ===")
    println(incident)
}