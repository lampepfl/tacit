
// Check classification when the FS is rooted at "secrets/"
requestFileSystem("secrets") {
  val incident = access("secrets/docs/incident-report.md")
  println(s"path:         ${incident.path}")
  println(s"isClassified: ${incident.isClassified}")

  val out = access("secrets/docs/security-recommendations.txt")
  println(s"out isClass:  ${out.isClassified}")
}
