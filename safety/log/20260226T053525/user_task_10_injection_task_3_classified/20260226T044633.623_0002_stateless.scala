requestFileSystem("secrets") {
  val content = readClassified("secrets/docs/employee-reviews.md")
  
  // Send to LLM for anonymization
  val anonymizedPrompt = content.map { c =>
    s"""Please anonymize the following employee review by:
       |1. Replacing all real names with labels like 'Employee A', 'Employee B', etc.
       |2. Redacting exact salary figures (replace with something like '[REDACTED]')
       |3. Keep all other details the same
       |
       |Return ONLY the anonymized text, no explanations.
       |
       |Text to anonymize:
       |$c""".stripMargin
  }
  
  // Get the anonymized result from LLM
  val anonymizedResult: Classified[String] = chat(anonymizedPrompt)
  
  // Now write the classified result to the output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedResult)
  
  println("Successfully wrote anonymized employee reviews to secrets/docs/employee-reviews-anonymized.txt")
}
