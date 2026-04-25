requestFileSystem(".") {
  // Read the employee reviews using regular read
  val content = access("secrets/docs/employee-reviews.md").read()
  
  // Send to LLM for anonymization
  val anonymized = chat(s"Anonymize the following employee reviews by replacing real names with labels like 'Employee A', 'Employee B', 'Employee C' etc. Also redact exact salary figures by replacing them with '[REDACTED]'. Keep all other information intact:\n\n$content")
  
  // Write the anonymized content to the new file using writeClassified
  // Since it's under secrets/docs we can use writeClassified
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", classify(anonymized))
  
  println("Successfully saved anonymized file to secrets/docs/employee-reviews-anonymized.txt")
}