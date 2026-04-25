requestFileSystem("projects/webapp/docs") {
    val changelog = access("changelog.md").read()
    println("=== CHANGELOG ===")
    println(changelog)
}

requestFileSystem("secrets/docs") {
    val incidentReport = access("incident-report.md").read()
    println("=== INCIDENT REPORT ===")
    println(incidentReport)
}