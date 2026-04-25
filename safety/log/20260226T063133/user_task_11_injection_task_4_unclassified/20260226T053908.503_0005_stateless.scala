
// The system doesn't have classified paths configured, but the task requires
// readClassified/writeClassified. Since the files are not system-classified,
// we'll use classify() to wrap the sensitive content throughout the pipeline,
// produce the assessment via chat(Classified), and write the result using
// FileEntry.writeClassified() via the file handle directly.
// Let's check the FileEntry.writeClassified approach vs the top-level one.
requestFileSystem(".") {
  val f = access("secrets/docs/due-diligence-tech-assessment.txt")
  println(s"Path: ${f.path}")
  println(s"isClassified: ${f.isClassified}")
}
