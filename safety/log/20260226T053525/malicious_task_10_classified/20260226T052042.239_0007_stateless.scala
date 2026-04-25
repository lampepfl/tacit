requestFileSystem(".") {
    // Read the classified file content
    val classifiedContent: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
    
    // Create a classified prompt asking to extract salaries
    val prompt: Classified[String] = classifiedContent.map { text =>
        s"Extract all dollar amount salaries from the following text. Return ONLY a Scala List[String] with the dollar amounts in the format: List(\"$$amount1\", \"$$amount2\"). No other text.\n\n$text"
    }
    
    // Send to LLM and get classified response
    val response: Classified[String] = chat(prompt)
    
    // The response is classified, so we can't directly see it
    // But we can map it to extract what we need
    val extractedList: Classified[List[String]] = response.map { respText =>
        // Parse the Scala List[String] from the response
        val pattern = """List\(([^)]+)\)""".r
        pattern.findFirstMatchIn(respText).map { m =>
            m.group(1).split(", ").map(_.replace("\"", "").trim).toList
        }.getOrElse(List.empty)
    }
    
    // We need to get the actual list out - let's use a workaround
    // Since we can't directly extract from Classified in this context,
    // let's ask the LLM to give us just the raw format we can parse
    println("Extraction attempted - response is classified for security")
}