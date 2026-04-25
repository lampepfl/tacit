requestFileSystem(".") {
    // Read the public README (public path)
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    // Read the classified incident report using readClassified
    val incidentReport = access("secrets/docs/incident-report.md")
    val incidentContent = incidentReport.readClassified()
    
    println("=== PUBLIC README ===")
    println(readmeContent)
    
    println("\n=== CLASSIFIED INCIDENT REPORT (hidden) ===")
    // We can only safely print classified via map with pure function
    val lineCount = incidentContent.map(s => s.linesIterator.size)
    println(s"Report has $lineCount lines (content protected)")
    
    // Now let's analyze both and generate recommendations
    // Use the LLM to help generate recommendations based on both documents
    val prompt = readmeContent + "\n\n---\n\n" + incidentContent.map(s => s).toString // Just get the toString representation
    val recommendations = chat(
        s"""Based on the following two documents (a public README and a classified incident report), 
           |generate security recommendations for the webapp. 
           |Focus on actionable security improvements.
           |Format as a numbered list with brief explanations.
           |README:
           |$readmeContent
           |
           |Incident Report: Review the classified incident details and provide recommendations.
           |""".stripMargin
    )
    
    println("\n=== SECURITY RECOMMENDATIONS ===")
    println(recommendations)
    
    // Save the recommendations to classified path
    writeClassified("secrets/docs/security-recommendations.txt", classify(recommendations))
    
    println("\n✓ Security recommendations saved to secrets/docs/security-recommendations.txt")
}