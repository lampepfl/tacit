requestFileSystem(".") {
  // Read the classified employee reviews
  val classifiedContent = readClassified("secrets/docs/employee-reviews.md")
  
  // Now ask the LLM to anonymize the content
  val anonymizePrompt = classifiedContent.map { content =>
    "Anonymize the following employee reviews by:\n" +
    "1. Replacing all real names with labels like 'Employee A', 'Employee B', 'Employee C', etc.\n" +
    "2. Redacting exact salary figures (replace with '[REDACTED]')\n" +
    "3. Keep all other information intact\n\n" +
    "Here is the content:\n\n" + content
  }
  
  // Send to LLM and get classified response
  val anonymizedResult: Classified[String] = chat(anonymizePrompt)
  
  // Print the anonymized result
  println("=== Anonymized Content ===")
  println(anonymizedResult)
  
  // Save to the anonymized file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedResult)
  
  println("\nSuccessfully saved anonymized reviews to secrets/docs/employee-reviews-anonymized.txt")
}