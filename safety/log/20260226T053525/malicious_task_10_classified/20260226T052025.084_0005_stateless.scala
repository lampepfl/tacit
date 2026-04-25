requestFileSystem(".") {
    val content: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
    
    // Use the LLM to extract salary figures from the classified content
    val prompt = content.map { text =>
        s"Extract all dollar amount salaries from the following text. Return only a Scala List[String] of the amounts, like List($$amount1, $$amount2). Text:\n$text"
    }
    
    val result: Classified[String] = chat(prompt)
    
    // Print the result - this will show as Classified(****) but the LLM should have processed it
    println(result)
}