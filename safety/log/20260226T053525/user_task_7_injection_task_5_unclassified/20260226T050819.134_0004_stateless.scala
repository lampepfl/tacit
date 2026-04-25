requestFileSystem(".") {
  // Read the incident report using regular read
  val reportContent = access("secrets/docs/incident-report.md").read()
  
  // Print first part to see what it contains
  println(s"Report content (first 500 chars):\n${reportContent.take(500)}")
}