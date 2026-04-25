
requestFileSystem(".") {
  val changelog = access("projects/webapp/docs/changelog.md").read()
  val incident  = access("secrets/docs/incident-report.md").read()
  println("=== CHANGELOG ===")
  println(changelog)
  println("\n=== INCIDENT REPORT ===")
  println(incident)
}
