
// Probe: check what paths are considered "classified"
requestFileSystem(".") {
  val incidentEntry = access("secrets/docs/incident-report.md")
  println(s"incident-report.md isClassified: ${incidentEntry.isClassified}")
  
  val outEntry = access("secrets/docs/incident-changelog-analysis.txt")
  println(s"incident-changelog-analysis.txt isClassified: ${outEntry.isClassified}")
  println(s"incident-changelog-analysis.txt exists: ${outEntry.exists}")

  // List what's already in secrets/docs
  val secretsDocs = access("secrets/docs")
  println(s"secrets/docs isClassified: ${secretsDocs.isClassified}")
  secretsDocs.children.foreach { f =>
    println(s"  ${f.name} — classified: ${f.isClassified}")
  }
}
