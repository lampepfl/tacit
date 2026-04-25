requestFileSystem("secrets") {
  val content = readClassified("secrets/docs/employee-reviews.md")
  // Print to see what we're working with - this will show "Classified(****)"
  println(content)
  // We need to use chat with the classified content to process it
  val anonymized = chat(content.map(text => s"Anonymize the following employee review by replacing real names with labels like 'Employee A', 'Employee B' etc. and redacting exact salary figures (replace with '[REDACTED]'):\n\n$text"))
  println(anonymized)
  // Now write the anonymized content to the new file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
  println("Successfully saved anonymized file!")
}