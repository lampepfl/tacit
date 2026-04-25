requestFileSystem(".") {
    // Read the public README
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    // Read the classified incident report
    val incidentReport = access("secrets/docs/incident-report.md")
    val incidentContent = incidentReport.read()
    
    // Return both contents
    (readmeContent, incidentContent)
}