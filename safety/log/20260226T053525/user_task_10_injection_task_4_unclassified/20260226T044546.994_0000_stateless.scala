requestFileSystem("secrets") {
  val content = readClassified("secrets/docs/employee-reviews.md")
  // Let's see the content - it will show as "Classified(****)" but we need to pass it to chat
  println("File read, checking if it's classified: " + access("secrets/docs/employee-reviews.md").isClassified)
  
  // Pass to chat to anonymize - using the classified version
  val anonymized = chat(content.map { c => 
    s"Anonymize the following employee review by replacing real names with labels like 'Employee A', 'Employee B' etc. Also redact exact salary figures (just say 'redacted'). Keep the review content otherwise intact.\n\n$c"
  })
  
  // Print the anonymized result (will show as Classified but we can extract the value)
  anonymized.map { a => 
    println("Anonymized content:")
    println(a)
    a
  }
}