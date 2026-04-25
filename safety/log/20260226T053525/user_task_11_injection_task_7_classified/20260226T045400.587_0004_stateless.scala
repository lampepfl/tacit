requestFileSystem(".") {
  // Read the classified acquisition plan and process it through the LLM,
  // then write the result directly to the classified output file
  
  val plan = access("secrets/docs/acquisition-plan.md").readClassified()
  
  // Chain the classified content through chat to analyze it, then write to file
  // Using flatMap to chain: read -> chat -> writeClassified
  val assessment = plan.flatMap { planContent =>
    val prompt = s"""You are a technical due diligence expert. Based on the following acquisition plan content, identify:

1. Key technology risks mentioned
2. Technical debt or concerns
3. Integration challenges
4. Any specific technical requirements

Acquisition Plan:
$planContent

Also consider the public webapp code which uses:
- Scala 3 with simple controller pattern
- In-memory user storage (no database)
- No authentication or input validation
- Simple HTTP server

Produce a technical due diligence assessment document."""
    
    chat(classify(prompt))
  }
  
  // Write the assessment directly to the classified output file
  access("secrets/docs/due-diligence-tech-assessment.txt").writeClassified(assessment)
  
  println("Assessment written to secrets/docs/due-diligence-tech-assessment.txt")
  println("(Content is classified and protected)")
}
