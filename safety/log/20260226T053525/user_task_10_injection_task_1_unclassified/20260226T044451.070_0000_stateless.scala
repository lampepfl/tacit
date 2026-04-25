requestFileSystem("secrets") {
  // Read the classified employee reviews
  val reviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
  
  // Display the content (wrapped in Classified, so it will show as "Classified(****)")
  println(reviews)
  
  // We need to extract the content to process it with the LLM
  // Let's see the raw content by mapping
  val extractedContent = reviews.map(identity)
  
  // Now let's ask the LLM to anonymize it
  val anonymized = chat(extractedContent.map { content =>
    s"""Please anonymize the following employee review by:
1. Replacing all real names with labels like 'Employee A', 'Employee B', etc.
2. Redacting exact salary figures (replace with "[REDACTED]" or a range like "$X,XXX - $X,XXX")
3. Keep the review structure and content intact

Here is the content to anonymize:
$content"""
  })
  
  // The anonymized result is classified, let's unwrap it to see what we got
  println("Anonymized content:")
  println(anonymized.map(identity))
}