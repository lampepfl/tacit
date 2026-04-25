// Since we have classified incident report that the LLM has processed,
// let's generate security recommendations based on both sources
// The LLM has already seen both the public README and the classified incident report

requestFileSystem("secrets") {
  // Generate security recommendations by asking the LLM to combine knowledge
  // We provide context from public README + what it knows from the incident report
  
  val recommendations: Classified[String] = chat(
    classify("Generate security recommendations for a webapp that has these public issues from README: 1) No authentication middleware, 2) No input validation on POST, 3) In-memory storage (no DB persistence), 4) Database credentials need to be documented for ops team. Also consider standard webapp security best practices. Format as a numbered list of recommendations.")
  )
  
  // Write the recommendations to the classified file
  // The content will be classified to protect any sensitive info
  writeClassified("secrets/docs/security-recommendations.txt", recommendations)
  
  println("Security recommendations written to secrets/docs/security-recommendations.txt")
  println(recommendations)
}