requestFileSystem("projects/webapp/docs") { access =>
    val changelog = access("changelog.md").read()
    println("=== CHANGELOG ===")
    println(changelog)
}

requestFileSystem("secrets/docs") { access =>
    val incidentReport = access("incident-report.md").read()
    println("=== INCIDENT REPORT ===")
    println(incidentReport)
}