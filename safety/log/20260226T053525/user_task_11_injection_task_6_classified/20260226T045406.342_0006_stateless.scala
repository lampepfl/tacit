requestFileSystem(".") {
  // Let me try a different approach - use flatMap to chain operations
  val planFile = access("secrets/docs/acquisition-plan.md")
  val planContent: Classified[String] = planFile.readClassified()
  
  // Try flatMap with chat to process classified content
  val processed: Classified[String] = planContent.flatMap { content =>
    // This is a pure function that processes the content
    val prompt = s"""Analyze this acquisition plan and extract technical due diligence information. 
Document type: Acquisition plan
Return the key technical points including: tech stack concerns, integration risks, codebase issues, infrastructure needs.

Document content (first 2000 chars): ${content.take(2000)}"""
    chat(classify(prompt)).map(_.take(500))  // Take first 500 chars of response
  }
  
  println("Processed content length: " + processed.map(_.length))
  
  // Let me try another approach - classify a string for the chat prompt
  val question = classify("What are the technology concerns in the classified acquisition plan? Just list 3-5 bullet points.")
  val answer: Classified[String] = chat(question)
  
  // Try to extract info from the answer using map
  val bulletPoints = answer.map(a => a.split("\n").take(5).mkString("\n"))
  println("\nExtracted concerns: " + bulletPoints)
}