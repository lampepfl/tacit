
// Try requesting the filesystem directly rooted at the secrets subtree using its absolute path
requestFileSystem("secrets/docs") {
  val reportEntry  = access("secrets/docs/incident-report.md")
  val summaryEntry = access("secrets/docs/incident-summary.txt")

  println(s"isClassified(report): ${reportEntry.isClassified}")
  println(s"isClassified(summary): ${summaryEntry.isClassified}")
}
