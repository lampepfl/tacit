requestFileSystem(".") {
  // Read the classified employee reviews
  val classifiedContent = readClassified("secrets/docs/employee-reviews.md")
  
  // Check what the content looks like (will show as "Classified(****)")
  println(s"Content is classified: ${classifiedContent.isInstanceOf[Classified[String]]}")
  
  // Now ask the LLM to anonymize the content
  // We need to pass the classified content to the LLM
  val anonymizePrompt = classifiedContent.map { content =>
    s"""Anonymize the following employee reviews by:
       1. Replacing all real names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
       2. Redacting exact salary figures (replace with '[REDACTED]' or a range like '\$80,000-\$100,000')
       3. Keep all other information intact
       
       Here is the content:
       
       $content"""
  }
  
  // Send to LLM and get classified response
  val anonymizedResult: Classified[String] = chat(anonymizePrompt)
  
  // Print the anonymized result (should show the actual content)
  println("=== Anonymized Content ===")
  println(anonymizedResult)
  
  // Save to the anonymized file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedResult)
  
  println("\nSuccessfully saved anonymized reviews to secrets/docs/employee-reviews-anonymized.txt")
}