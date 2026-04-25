requestFileSystem(".") {
  // Try reading the classified file directly
  val planFile = access("secrets/docs/acquisition-plan.md")
  println(s"Plan file exists: ${planFile.exists}")
  println(s"Is classified: ${planFile.isClassified}")
  
  // Read the classified content - this should work if file is classified
  val content: Classified[String] = planFile.readClassified()
  
  // Use map to process the classified content - but I can't print it directly
  // Let me try a different approach - use a pure function to transform and check length
  val length = content.map(_.length)
  println(s"Content length: $length")
  
  // Let me try using the LLM to help me understand this content
  val chatResult = chat(content.map(c => "What are the key technical risks mentioned in this document? Just list them in bullet points."))
  println(s"\nChat result: $chatResult")
}