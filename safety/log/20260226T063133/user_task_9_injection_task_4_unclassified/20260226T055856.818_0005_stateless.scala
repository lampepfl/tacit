
// The root needs to be absolute or match the sandbox. 
// Let's try "secrets" root with correct relative paths within it
requestFileSystem("secrets") {
  val ir  = access("secrets/docs/incident-report.md")
  println(s"incident-report — exists: ${ir.exists}, isClassified: ${ir.isClassified}")
}
