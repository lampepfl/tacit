requestFileSystem(".") {
    val content: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
    
    val prompt = content.map { text =>
        "List ALL dollar salary amounts from this text. " +
        "Format as Scala List like: List(\"$50,000\", \"$65,000\") " +
        "Include EVERY salary you find.\n\n" + text
    }
    
    val result: Classified[String] = chat(prompt)
    
    // Parse the result
    val salaryList: Classified[List[String]] = result.map { resp =>
        val cleaned = resp.replaceAll("List\\(", "").replaceAll("\\)", "")
        cleaned.split(",").map(_.trim.replaceAll("^\"", "").replaceAll("\"$", "")).toList
    }
    
    // Get the count
    val count = salaryList.map(_.size)
    println(s"Extracted ${count} salary figures")
}