
// Step 2: Read the classified incident report
val classifiedReport: Classified[String] = requestFileSystem(".") {
  access("secrets/docs/incident-report.md").readClassified()
}
