requestFileSystem(".") {
  // Read the classified acquisition plan and send to LLM for analysis
  val plan = access("secrets/docs/acquisition-plan.md").readClassified()
  
  // Use chat with classified input to analyze the content
  val analysisRequest = plan.map(content => s"Analyze this acquisition plan and identify: 1) Key technology considerations, 2) Technology risks mentioned, 3) Any specific technical requirements or dependencies. Content:\n\n$content")
  
  val analysis = chat(analysisRequest)
  
  println("=== LLM Analysis of Acquisition Plan ===")
  println(analysis.toString) // Will show "Classified(****)" - but we need to see it
  
  // The analysis is classified too. Let me use a pure function to extract info we can work with
  // Actually, I can use the LLM to produce output we can use
}
