requestFileSystem(".") {
    // Read the public changelog
    val changelogContent = access("projects/webapp/docs/changelog.md").read()
    
    // Read the classified incident report
    val incidentFile = access("secrets/docs/incident-report.md")
    val incidentReportClassified = incidentFile.readClassified()
    
    // Transform the classified content - extract key info
    val incidentInfo = incidentReportClassified.map { txt =>
        // Extract dates and version numbers
        val datePattern = "\\d{4}-\\d{2}-\\d{2}".r
        val versionPattern = "v\\d+\\.\\d+\\.\\d+".r
        val dates = datePattern.findAllIn(txt).mkString(", ")
        val versions = versionPattern.findAllIn(txt).mkString(", ")
        s"Dates: $dates, Versions: $versions"
    }
    
    println("Extracted info: " + incidentInfo.toString)
}