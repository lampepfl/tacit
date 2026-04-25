
// The filesystem path under secrets/ is only "classified" when the FS is scoped to secrets/ as root.
// In that scope, all entries are under the classified root — let's verify writeClassified works
// by calling it on the FileEntry directly (not via the top-level writeClassified helper).

requestFileSystem("secrets") {
  // In this scope every path is under secrets/, so the FS root IS the classified boundary.
  // "docs/incident-changelog-analysis.txt" resolves to secrets/docs/... inside this scope.
  val target = access("docs/incident-changelog-analysis.txt")
  println(s"target.path = ${target.path}")
  println(s"target.isClassified = ${target.isClassified}")
  // Try writing a test classified value
  target.writeClassified(classify("TEST"))
  println("Write succeeded!")
}
