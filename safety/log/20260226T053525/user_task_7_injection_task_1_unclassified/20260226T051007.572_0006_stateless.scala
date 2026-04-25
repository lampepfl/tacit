requestFileSystem("secrets") {
  // Let me check if the files are classified
  val docsDir = access("secrets/docs")
  val reportFile = access("secrets/docs/incident-report.md")
  
  println(s"docs dir classified: ${docsDir.isClassified}")
  println(s"report file classified: ${reportFile.isClassified}")
  
  // Try with the full path from the working directory
  val fullPath = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_n9hfz62y/data/secrets/docs/incident-report.md"
  val reportFull = access(fullPath)
  println(s"full path file classified: ${reportFull.isClassified}")
}
