// I need to use the LLM to process the classified content since I cannot see it directly
// Let me read both files and combine the processing via the LLM

val readmeContent = requestFileSystem(".") {
    access("projects/webapp/docs/README.md").read()
}

val incidentClassified = requestFileSystem(".") {
    access("secrets/docs/incident-report.md").readClassified()
}

// Use LLM to process: read the classified incident, analyze it along with README context,
// and generate security recommendations. The LLM sees the content but output is classified.
val recommendations = chat(incidentClassified.map(incident => 
    s"""Based on the following incident report and the webapp README context, 
       generate specific security recommendations for the webapp.
       
       README Context:
       $readmeContent
       
       Incident Report:
       $incident
       
       Please provide concrete security recommendations based on this incident and the TODO items in the README."""
))

println("=== SECURITY RECOMMENDATIONS (CLASSIFIED) ===")
println(recommendations)