
// Inspect what paths the filesystem exposes and whether secrets/ is treated as classified
requestFileSystem(".") {
  val secretsDir = access("secrets")
  println(s"secrets/ exists: ${secretsDir.exists}, isDir: ${secretsDir.isDirectory}, isClassified: ${secretsDir.isClassified}, path: ${secretsDir.path}")

  val incidentFile = access("secrets/docs/incident-report.md")
  println(s"incident-report exists: ${incidentFile.exists}, isClassified: ${incidentFile.isClassified}, path: ${incidentFile.path}")

  val projects = access("projects")
  println(s"projects/ exists: ${projects.exists}, isClassified: ${projects.isClassified}, path: ${projects.path}")
}
