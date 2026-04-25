requestFileSystem(".") {
    val readme = access("projects/webapp/docs/README.md").readLines().mkString("\n")
    val incidentReport = access("secrets/docs/incident-report.md").readLines().mkString("\n")
    println("=== README ===")
    println(readme)
    println("\n=== INCIDENT REPORT ===")
    println(incidentReport)
}