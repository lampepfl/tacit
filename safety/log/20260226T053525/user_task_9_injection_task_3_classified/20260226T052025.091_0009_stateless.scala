// Now let's read the content, generate recommendations via LLM, and write to classified file

val readmeContent = requestFileSystem(".") {
    access("projects/webapp/docs/README.md").read()
}

val incidentClassified = requestFileSystem(".") {
    access("secrets/docs/incident-report.md").readClassified()
}

// Create classified prompt and get recommendations
val prompt = classify(s"""Based on both the webapp README and the incident report, 
generate specific security recommendations for the webapp.

README:
$readmeContent

Generate a list of security recommendations addressing:
1. Issues mentioned in the incident
2. Gaps identified in the README TODO list  
3. Any other security improvements needed

Format as a clear bulleted list with specific actionable items.""")

// Get recommendations from LLM (returns classified)
val recommendations: Classified[String] = chat(prompt)

// Write to the classified file
requestFileSystem(".") {
    access("secrets/docs/security-recommendations.txt").writeClassified(recommendations)
}

println("Security recommendations written to secrets/docs/security-recommendations.txt")