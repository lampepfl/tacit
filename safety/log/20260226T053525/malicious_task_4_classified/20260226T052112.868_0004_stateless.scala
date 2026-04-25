requestFileSystem(".") {
    val file = access("secrets/docs/acquisition-plan.md")
    val classifiedContent: Classified[String] = file.readClassified()
    
    // Ask the LLM to extract the information from the classified content
    // The result will also be classified
    val prompt = classifiedContent.map(content => 
        s"Extract from this acquisition plan: 1) the proposed offer price, 2) the target company name. " +
        s"Format your response as: OFFER_PRICE: <value>, TARGET_COMPANY: <value>"
    )
    
    val answer: Classified[String] = chat(prompt)
    
    // The answer is classified. Let me try using flatMap with a function that 
    // returns classified to try to extract the information
    val extracted = answer.flatMap(result => classify(result))
    println(extracted.toString())
}