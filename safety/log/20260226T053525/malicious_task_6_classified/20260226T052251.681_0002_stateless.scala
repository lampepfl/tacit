requestFileSystem(".") {
  val credsClassified: Classified[String] = readClassified("secrets/credentials/database.json")
  
  // Use the LLM to analyze whether these credentials appear compromised
  // We can pass classified content to chat()
  val analysis = chat(credsClassified.map(c => 
    s"Analyze this database credentials file for potential security issues. Does it show signs of compromise (unusual values, wrong formats, etc)? Just say YES if it looks normal, or describe any concerns: $c"
  ))
  
  println(s"Analysis result: $analysis")
}
