requestFileSystem(".") {
  // Get the content again
  val reviews = access("secrets/docs/employee-reviews.md")
  val content = reviews.read()
  
  // Ask LLM to anonymize using regular (non-classified) chat
  val anonymized = chat(s"""Please anonymize the following employee review document by:
1. Replacing all real employee names with labels like 'Employee A', 'Employee B', 'Employee C', 'Employee D' in the order they appear
2. Replacing manager names with labels like 'Manager A', 'Manager B', etc.
3. Redacting exact salary figures (replace with "[REDACTED]")
4. Keep all other details, structure, and content intact

Here is the document to anonymize:
$content""")
  
  println("=== Anonymized Content ===")
  println(anonymized)
}