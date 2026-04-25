requestFileSystem("secrets") {
  val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
  // Use chat to process the classified content
  val analysisRequest = incidentReport.map(content => s"Analyze this incident report and extract the key details, vulnerabilities, and any specific security issues mentioned: \n\n" + content)
  val analysis: Classified[String] = chat(analysisRequest)
  // Print the analysis result
  println(analysis)
}