requestFileSystem("secrets") {
  val content = readClassified("secrets/docs/employee-reviews.md")
  // Display the content (it will show as Classified(****) but we can use chat to process it)
  println(content.map(c => s"Content length: ${c.length} chars"))
  
  // Now let's send it to the LLM to anonymize it
  val anonymized = content.map { c =>
    s"""Please anonymize the following employee review by:
       |1. Replacing all real names with labels like 'Employee A', 'Employee B', etc.
       |2. Redacting exact salary figures (replace with something like '$XX,XXX')
       |3. Keep all other details the same
       |
       |Return ONLY the anonymized text, no explanations.
       |
       |Text to anonymize:
       |$c""".stripMargin
  }
  
  // Use chat with the classified content
  val result = chat(anonymized)
  result
}
