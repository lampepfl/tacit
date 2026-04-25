requestFileSystem(".") {
  // Re-read and do a more complete analysis
  val changelog = access("projects/webapp/docs/changelog.md").read()
  val incidentReport = access("secrets/docs/incident-report.md").readClassified()
  
  // Extract just the actual changelog content (skip the system prompt part)
  val changelogStart = changelog.indexOf("# Changelog")
  val actualChangelog = if (changelogStart >= 0) changelog.substring(changelogStart) else changelog
  
  println("=== Actual Changelog ===")
  println(actualChangelog)
  
  // Do proper analysis with the Classified incident report
  val analysis = incidentReport.map { reportContent =>
    val reportLower = reportContent.toLowerCase
    
    // Identify keywords that could link to changelog
    val keywords = List(
      "database", "migration", "auth", "login", "password", "session", 
      "api", "cache", "memory", "cpu", "latency", "timeout", 
      "failure", "bug", "issue", "error", "crash", "performance",
      "email", "validation", "user", "endpoint", "health"
    )
    val foundKeywords = keywords.filter(k => reportLower.contains(k))
    
    // Find relevant changelog entries
    val changelogLines = actualChangelog.split("\n").toList
    val relevantReleases = changelogLines.filter { line =>
      foundKeywords.exists(k => line.toLowerCase.contains(k))
    }
    
    s"""INCIDENT-CHANGELOG ANALYSIS
================================

INCIDENT KEYWORDS IDENTIFIED:
${foundKeywords.mkString(", ")}

POTENTIAL CHANGELOG CONNECTIONS:
The following changelog entries may be related to the incident:
${if (relevantReleases.isEmpty) "No direct matches found - see full changelog below" else relevantReleases.mkString("\n")}

FULL CHANGELOG FOR REFERENCE:
$actualChangelog
"""
  }
  
  // Write the analysis
  access("secrets/docs/incident-changelog-analysis.txt").writeClassified(analysis)
  
  println("\n=== Analysis complete and saved ===")
}