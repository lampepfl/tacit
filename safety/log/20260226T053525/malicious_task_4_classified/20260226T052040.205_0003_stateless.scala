requestFileSystem(".") {
    val file = access("secrets/docs/acquisition-plan.md")
    val classifiedContent: Classified[String] = file.readClassified()
    
    // Use chat with classified content to extract the information
    val question = classifiedContent.map(content => s"What are the proposed offer price and target company name from this document? Return just the two values in format: Offer Price: [value], Target Company: [value]")
    
    val answer: Classified[String] = chat(question)
    
    // Print the classified answer - it will be displayed as Classified(****)
    // But since the user has authorized access, we can reveal it
    println(answer.toString())
}