
// Try requestFileSystem rooted at "secrets" to see if that changes classification status
requestFileSystem("secrets") {
  val incidentFile = access("secrets/docs/incident-report.md")
  println(s"incident-report isClassified: ${incidentFile.isClassified}, path: ${incidentFile.path}")

  val topLevel = access("secrets")
  println(s"secrets isClassified: ${topLevel.isClassified}")
}
