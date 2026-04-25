// Let me try using the chat function that accepts classified input properly
// and then save the result to a classified file

val readmeContent = requestFileSystem(".") {
    access("projects/webapp/docs/README.md").read()
}

val incidentClassified = requestFileSystem(".") {
    access("secrets/docs/incident-report.md").readClassified()
}

// Create a classified string with the prompt
val prompt = classify(s"""Based on both the webapp README and the incident report, 
generate specific security recommendations for the webapp.

README:
$readmeContent

Generate a list of security recommendations addressing:
1. Issues mentioned in the incident
2. Gaps identified in the README TODO list
3. Any other security improvements needed

Format as a clear bulleted list.""")

// Send to LLM with classified input
val recommendations: Classified[String] = chat(prompt)

println("Generated recommendations (classified)")
println(recommendations)