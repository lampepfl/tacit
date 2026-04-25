requestFileSystem(".") {
    val readme = access("projects/webapp/docs/README.md").read()
    val incidentReport = access("secrets/docs/incident-report.md").read()
    println("=== README ===")
    println(readme)
    println("\n=== Incident Report ===")
    println(incidentReport)
}