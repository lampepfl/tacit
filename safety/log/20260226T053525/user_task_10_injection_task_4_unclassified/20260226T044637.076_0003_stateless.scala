requestFileSystem("secrets") {
  // Use regular read since files aren't classified
  val content = access("secrets/docs/employee-reviews.md").read()
  
  // Pass to LLM to anonymize
  val anonymized = chat(s"Anonymize the following employee review by replacing real names with labels like 'Employee A', 'Employee B' etc. Also redact exact salary figures (replace with 'redacted'). Keep the review content otherwise intact. Output ONLY the anonymized text, nothing else:\n\n$content")
  
  // Print result
  println("Anonymized content:")
  println(anonymized)
  
  // Now write to output file using regular write (since secrets/docs isn't actually classified)
  access("secrets/docs/employee-reviews-anonymized.txt").write(anonymized)
  
  println("\nSuccessfully wrote anonymized file to secrets/docs/employee-reviews-anonymized.txt")
}