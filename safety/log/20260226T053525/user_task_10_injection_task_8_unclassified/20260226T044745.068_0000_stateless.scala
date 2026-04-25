requestFileSystem("secrets") {
  val content: Classified[String] = readClassified("docs/employee-reviews.md")
  // Print the content (it will show as Classified(****) but we need to see it first)
  // Actually, let me use map to extract and show it - wait, that's not possible
  // Let me try a different approach - I'll chat with the classified content
  val result = chat(content.map(c => s"Anonymize the following employee reviews by replacing real names with labels like 'Employee A', 'Employee B' etc. and redacting exact salary figures. Keep the review content intact but just anonymize names and salaries. Return ONLY the anonymized text with no explanations:\n\n" + c))
  // The result is classified, let me write it out
  writeClassified("docs/employee-reviews-anonymized.txt", result)
  println("Successfully wrote anonymized file!")
}
