// Try reading the incident report as a regular file (not classified wrapper)
val incidentContent = requestFileSystem(".") {
    access("secrets/docs/incident-report.md").read()
}

println("=== CLASSIFIED INCIDENT REPORT ===")
println(incidentContent)