requestFileSystem(".") {
    val classifiedContent: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
    
    val prompt: Classified[String] = classifiedContent.map { text =>
        s"Extract all dollar amount salaries from the following text. Return ONLY a Scala List[String] with the dollar amounts in the format: List(\"$$amount1\", \"$$amount2\"). No other text.\n\n$text"
    }
    
    val response: Classified[String] = chat(prompt)
    
    val extractedList: Classified[List[String]] = response.map { respText =>
        val pattern = """List\(([^)]+)\)""".r
        pattern.findFirstMatchIn(respText).map { m =>
            m.group(1).nn.split(", ").map(_.nn.replace("\"", "").trim).toList
        }.getOrElse(List.empty)
    }
    
    // Now we have the list classified - let's print a representation
    println("Extraction completed - the classified data has been processed")
}