requestFileSystem(".") {
  // Read the employee reviews
  val content = access("secrets/docs/employee-reviews.md").read()
  
  // Send to LLM to anonymize
  val prompt = "Anonymize the following employee reviews by:\n" +
    "1. Replacing all real names with labels like 'Employee A', 'Employee B', 'Employee C', etc.\n" +
    "2. Redacting exact salary figures (replace with '[REDACTED]')\n" +
    "3. Keep all other information intact\n\n" +
    "Here is the content:\n\n" + content
  
  val anonymizedResult = chat(prompt)
  
  // Print the anonymized result
  println("=== Anonymized Content ===")
  println(anonymizedResult)
  
  // Save to the output file
  access("secrets/docs/employee-reviews-anonymized.txt").write(anonymizedResult)
  
  println("\n=== Successfully saved anonymized reviews ===")
}