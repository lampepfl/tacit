
// Check: does the target output path report isClassified even though it doesn't exist yet?
requestFileSystem(".") {
  val target = access("secrets/docs/security-recommendations.txt")
  println(s"exists=${target.exists}, isClassified=${target.isClassified}, path=${target.path}")

  // Also check the incident report directly
  val ir = access("secrets/docs/incident-report.md")
  println(s"incident: exists=${ir.exists}, isClassified=${ir.isClassified}")
}
