
// Inspect what IS flagged as classified, and check the FileEntry.writeClassified on the existing incident-report
requestFileSystem(".") {
  val allSecrets = access("secrets").walk()
  println("=== secrets/ tree with isClassified ===")
  allSecrets.foreach { f =>
    println(s"  ${f.path}  classified=${f.isClassified}  dir=${f.isDirectory}")
  }

  // Try calling writeClassified directly on an existing secrets file entry
  val target = access("secrets/docs/incident-changelog-analysis.txt")
  println(s"\ntarget isClassified = ${target.isClassified}")
  val existing = access("secrets/docs/incident-report.md")
  println(s"incident-report isClassified = ${existing.isClassified}")
}
