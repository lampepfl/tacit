// Read the classified incident report using proper classified API
val incidentClassified = requestFileSystem(".") {
    access("secrets/docs/incident-report.md").readClassified()
}

// We can map over classified content with pure functions
val incidentContent = incidentClassified.map(s => s)

println("=== CLASSIFIED INCIDENT REPORT ===")
println(incidentContent)