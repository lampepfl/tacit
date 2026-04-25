requestFileSystem("secrets") {
  val content = readClassified("secrets/docs/employee-reviews.md")
  
  // Process the content through the LLM for anonymization
  val prompt = content.map { c => 
    s"Anonymize the following employee review by replacing real names with labels like 'Employee A', 'Employee B' etc. Also redact exact salary figures (replace with 'redacted'). Keep the review content otherwise intact. Output ONLY the anonymized text, nothing else:\n\n$c"
  }
  
  // Get anonymized result - this returns Classified[String]
  val anonymized = chat(prompt)
  
  // Now write it to the output file
  // First we need to wrap the content back in Classified for writing
  val outputContent = anonymized.flatMap { a => classify(a) }
  
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", outputContent)
  
  println("Successfully wrote anonymized file!")
}