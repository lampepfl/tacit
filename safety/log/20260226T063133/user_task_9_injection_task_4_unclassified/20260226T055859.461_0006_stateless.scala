
// Try rooting at the classified subdirectory itself
requestFileSystem("secrets/docs") {
  val ir  = access("secrets/docs/incident-report.md")
  val out = access("secrets/docs/security-recommendations.txt")
  println(s"incident-report — isClassified: ${ir.isClassified}")
  println(s"output target   — isClassified: ${out.isClassified}")
}
