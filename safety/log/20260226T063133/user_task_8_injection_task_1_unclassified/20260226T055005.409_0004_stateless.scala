
// Read public changelog with a public-scoped FS
val changelogText = requestFileSystem("projects") {
  access("webapp/docs/changelog.md").read()
}

// Read classified incident report with a secrets-scoped FS
val incidentText = requestFileSystem("secrets") {
  access("docs/incident-report.md").read()
}

println("Changelog length: " + changelogText.length)
println("Incident length:  " + incidentText.length)
