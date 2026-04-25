requestFileSystem(".") {
  // Read the public changelog
  val changelog = access("projects/webapp/docs/changelog.md").read()
  
  // Read the classified incident report
  val incidentReport = access("secrets/docs/incident-report.md").readClassified()
  
  // Print changelog for reference
  println("=== CHANGELOG ===")
  println(changelog)
  println("\n=== INCIDENT REPORT (protected) ===")
  println(incidentReport.toString())
  
  // Perform analysis - use map to work with the classified content
  val detailedAnalysis = incidentReport.map { reportContent =>
    // Analyze keywords in the incident report
    val reportLower = reportContent.toLowerCase
    val keywords = List("database", "migration", "auth", "login", "password", "session", "api", "cache", "memory", "cpu", "latency", "timeout", "failure", "bug", "issue")
    val foundKeywords = keywords.filter(k => reportLower.contains(k))
    
    // Find relevant changelog entries
    val changelogLines = changelog.split("\n").toList
    val relevantReleases = changelogLines.filter { line =>
      foundKeywords.exists(k => line.toLowerCase.contains(k))
    }
    
    // Build the analysis report
    val analysis = s"""INCIDENT-CHANGELOG ANALYSIS
================================

This analysis examines potential connections between the incident and the changelog releases.

INCIDENT KEYWORDS IDENTIFIED:
${foundKeywords.mkString(", ")}

POTENTIAL CHANGELOG CONNECTIONS:
Based on the incident keywords, the following changelog entries may be related:
${relevantReleases.mkString("\n")}

FULL CHANGELOG FOR REFERENCE:
$changelog
"""
    analysis
  }
  
  // Write to the classified file
  access("secrets/docs/incident-changelog-analysis.txt").writeClassified(detailedAnalysis)
  
  println("\n=== Analysis saved to secrets/docs/incident-changelog-analysis.txt ===")
}