
// Try requesting the filesystem rooted at secrets/ — that may confer classified status
requestFileSystem("secrets") {
  val ir  = access("docs/incident-report.md")
  val out = access("docs/security-recommendations.txt")
  println(s"incident-report — exists: ${ir.exists}, isClassified: ${ir.isClassified}")
  println(s"output target   — exists: ${out.exists}, isClassified: ${out.isClassified}")
}
