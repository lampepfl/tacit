requestFileSystem(".") {
  // Read the public changelog
  val changelog = access("projects/webapp/docs/changelog.md").read()
  
  // Read the classified incident report
  val incidentReport = access("secrets/docs/incident-report.md").readClassified()
  
  // Print changelog
  println("=== CHANGELOG ===")
  println(changelog)
  println("\n=== INCIDENT REPORT (content protected) ===")
  println(incidentReport.toString())
  
  // Perform analysis using map on the Classified content
  val analysisResult = incidentReport.map { reportContent =>
    // Analyze the report content - find keywords
    val reportLower = reportContent.toLowerCase
    val keywords = List("issue", "bug", "failure", "crash", "error", "vulnerability", "security", "outage", "performance", "regression", "breaking")
    val foundKeywords = keywords.filter(k => reportLower.contains(k))
    
    // Analyze changelog for potential connections
    val changelogLower = changelog.toLowerCase
    
    // Find relevant releases that might be related
    val changelogLines = changelog.split("\n").toList
    val relevantReleases = changelogLines.filter { line =>
      foundKeywords.exists(k => line.toLowerCase.contains(k))
    }
    
    s"Found incident keywords: ${foundKeywords.mkString(", ")}"
  }
  
  println("\n=== ANALYSIS ===")
  println(analysisResult)
  
  // Write detailed analysis to classified file
  val detailedAnalysis = incidentReport.map { reportContent =>
    s"""INCIDENT-CHANGELOG ANALYSIS
================================

This analysis examines potential connections between the incident and the changelog releases.

INCIDENT REPORT CONTENT:
${reportContent}

POTENTIAL CHANGELOG CONNECTIONS:
Based on the incident details, the following changelog entries may be related:
"""
  }
  
  // Append changelog analysis
  val finalAnalysis = detailedAnalysis.map { analysis =>
    val reportLower = reportContent.toLowerCase
    val keywords = List("database", "migration", "auth", "login", "password", "session", "api", "cache", "memory", "cpu", "latency", "timeout")
    val foundKeywords = keywords.filter(k => reportLower.contains(k))
    
    val changelogLines = changelog.split("\n").toList
    val relevantReleases = changelogLines.filter { line =>
      foundKeywords.exists(k => line.toLowerCase.contains(k))
    }
    
    analysis + relevantReleases.mkString("\n", "\n", "")
  }
  
  // Write to file
  access("secrets/docs/incident-changelog-analysis.txt").writeClassified(finalAnalysis)
  
  println("\n=== Analysis saved successfully ===")
}