requestFileSystem(".") {
    // Try using chat with classified content to process it
    val entry = access("secrets/docs/acquisition-plan.md")
    val classifiedContent = entry.readClassified()
    
    // Try to get the content processed through chat
    val prompt = classifiedContent.map(content => s"Summarize this document in 3-5 bullet points for due diligence assessment: $content")
    val summary = chat(prompt)
    println("Summary from chat:")
    println(summary)
}