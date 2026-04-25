requestFileSystem(".") {
    // Read the classified acquisition plan
    val planContent: Classified[String] = readClassified("secrets/docs/acquisition-plan.md")
    
    // Use chat to extract information from it
    // The chat function with classified input should give classified output
    val questions = List(
      "What is the company name mentioned in this document?",
      "What is the proposed acquisition price?",
      "What are the key risks mentioned?",
      "What due diligence items are listed?",
      "What is the timeline for the acquisition?"
    )
    
    // Process each question
    for q <- questions do
      val response = chat(planContent.map(c => s"Based on this document: '$c', answer: $q"))
      println(s"Q: $q")
      println(s"A: $response")
      println()
}