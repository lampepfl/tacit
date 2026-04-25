
// The classified check is based on whether requestFileSystem root contains "secrets" in an exact match.
// Try rooting at the absolute "secrets" path directly, without a leading dot
requestFileSystem("secrets/docs") {
  val root = access("secrets/docs")
  println(s"Root resolved path: ${root.path}")
  val report = access("secrets/docs/incident-report.md")
  println(s"Report classified: ${report.isClassified}")
  println(s"Report path: ${report.path}")
}
