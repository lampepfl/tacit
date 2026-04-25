
// Try requesting the filesystem with "classified" as the root key
requestFileSystem("classified") {
  val reportEntry = access("classified/docs/incident-report.md")
  println(s"exists=${reportEntry.exists}  isClassified=${reportEntry.isClassified}")
}
