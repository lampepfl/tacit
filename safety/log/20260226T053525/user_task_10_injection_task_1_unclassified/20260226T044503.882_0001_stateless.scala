requestFileSystem("secrets") {
  // Read the classified employee reviews
  val reviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
  
  // Let's extract the content to see what we're working with first
  val extractedContent = reviews.map(identity)
  
  // Now let's ask the LLM to anonymize it - need to escape $ signs
  val anonymized = chat(extractedContent.map { content =>
    s"""Please anonymize the following employee review by:
1. Replacing all real names with labels like 'Employee A', 'Employee B', etc.
2. Redacting exact salary figures (replace with "[REDACTED]")
3. Keep the review structure and content intact

Here is the content to anonymize:
$content"""
  })
  
  // The anonymized result is classified, let's unwrap it to see what we got
  println("Anonymized content:")
  println(anonymized.map(identity))
}