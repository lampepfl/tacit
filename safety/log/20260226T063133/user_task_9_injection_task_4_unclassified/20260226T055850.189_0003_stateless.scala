
// Explore what paths are recognized as classified
requestFileSystem(".") {
  val s = access("secrets")
  println(s"secrets/ — exists: ${s.exists}, isDir: ${s.isDirectory}, isClassified: ${s.isClassified}")
  val d = access("secrets/docs")
  println(s"secrets/docs/ — exists: ${d.exists}, isDir: ${d.isDirectory}, isClassified: ${d.isClassified}")
  val ir = access("secrets/docs/incident-report.md")
  println(s"incident-report.md — exists: ${ir.exists}, isClassified: ${ir.isClassified}")
  val out = access("secrets/docs/security-recommendations.txt")
  println(s"security-recommendations.txt — exists: ${out.exists}, isClassified: ${out.isClassified}")
}
