
// Inspect the RealFileSystem source logic: what prefix makes isClassified = true?
// The error message says "classified paths" — let's see if an existing classified
// file reveals the prefix by checking isClassified on every file in secrets/
requestFileSystem(".") {
  // Try creating the output file first with a plain write, then checking isClassified
  val candidate = access("secrets/docs/incident-changelog-analysis.txt")
  candidate.write("test")
  println(s"After plain write — isClassified: ${candidate.isClassified}, exists: ${candidate.exists}")

  // Also check the existing files' isClassified status
  access("secrets/docs").children.foreach { e =>
    println(s"  ${e.name} isClassified=${e.isClassified}")
  }
}
