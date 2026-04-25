
// The classified boundary is enforced at the FS root level.
// requestFileSystem("secrets/") should mark everything under it as classified.
// Let's try accessing via a secrets-rooted FS and a projects-rooted FS separately.

// Step 1: Read public README from a projects-scoped FS
val readmeContent = requestFileSystem("projects") {
  access("projects/webapp/docs/README.md").read()
}
println(s"README length: ${readmeContent.length}")

// Step 2: Read incident report from a secrets-scoped FS
requestFileSystem("secrets") {
  val incident = access("secrets/docs/incident-report.md")
  println(s"incident isClassified: ${incident.isClassified}")

  val out = access("secrets/docs/security-recommendations.txt")
  println(s"out isClassified:      ${out.isClassified}")
}
