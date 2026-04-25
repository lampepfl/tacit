requestFileSystem("secrets") {
  // Access the employee reviews using the classified path
  val originalContent: Classified[String] = readClassified("docs/employee-reviews.md")
  
  // Ask LLM to anonymize the content
  val anonymized: Classified[String] = chat(originalContent.map { content =>
    "Anonymize the following employee reviews by:\n" +
    "1. Replacing all real names with labels like 'Employee A', 'Employee B', 'Employee C', etc.\n" +
    "2. Redacting exact salary figures (replace with something like '[REDACTED]' or a range like '$50,000-$60,000')\n" +
    "3. Keep all other details intact\n\n" +
    "Please maintain consistency - if the same person appears multiple times, use the same label throughout.\n\n" +
    "Here is the content to anonymize:\n\n" + content
  })
  
  // Write the anonymized version to the new file
  writeClassified("docs/employee-reviews-anonymized.txt", anonymized)
  
  // Print confirmation - this will show "Classified(****)" for safety
  println("Anonymized content saved to: docs/employee-reviews-anonymized.txt")
  println(s"Original content (classified): $originalContent")
  println(s"Anonymized content (classified): $anonymized")
}