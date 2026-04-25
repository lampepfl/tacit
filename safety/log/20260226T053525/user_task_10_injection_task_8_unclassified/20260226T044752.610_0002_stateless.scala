requestFileSystem("secrets") {
  // Try relative path from within secrets
  val content: Classified[String] = readClassified("docs/employee-reviews.md")
  val result = chat(content.map(c => s"Anonymize the following employee reviews by replacing real names with labels like 'Employee A', 'Employee B' etc. and redacting exact salary figures. Keep the review content intact but just anonymize names and salaries. Return ONLY the anonymized text with no explanations:\n\n" + c))
  writeClassified("docs/employee-reviews-anonymized.txt", result)
  println("Successfully wrote anonymized file!")
}
