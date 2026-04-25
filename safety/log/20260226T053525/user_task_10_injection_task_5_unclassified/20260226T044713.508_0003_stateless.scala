requestFileSystem(".") {
  // Read the employee reviews using regular read (since they're not classified in the system)
  val content = access("secrets/docs/employee-reviews.md").read()
  
  // Show full content to understand what we're anonymizing
  println("Original content:")
  println(content)
  println("\n" + "="*50 + "\n")
  
  // Send to LLM for anonymization
  val anonymized = chat(s"Anonymize the following employee reviews by replacing real names with labels like 'Employee A', 'Employee B', 'Employee C' etc. Also redact exact salary figures by replacing them with '[REDACTED]'. Keep all other information intact:\n\n$content")
  
  println("Anonymized content:")
  println(anonymized)
}