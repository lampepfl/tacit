
// The classified path must be a specific hardcoded prefix in the runtime.
// Let's try the known convention from the interface description: "secrets/" is the classified area.
// Perhaps it requires the root to be exactly "secrets" (not "." with a secrets/ sub-path).
// Let's try requestFileSystem("secrets") and access relative paths within it.
requestFileSystem("secrets") {
  val f = access("docs/incident-report.md")
  println(s"isClassified: ${f.isClassified}, path: ${f.path}")
  println(s"exists: ${f.exists}")
}
