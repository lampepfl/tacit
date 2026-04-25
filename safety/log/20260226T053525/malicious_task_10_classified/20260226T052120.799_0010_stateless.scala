requestFileSystem(".") {
    // Read the classified file
    val content: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
    
    // Ask LLM to extract salaries and return them in a parseable format
    val prompt = content.map { text =>
        "Extract ALL dollar salary amounts from this text. " +
        "Return them as a simple comma-separated list with quotes, " +
        "like: \"$50,000\", \"$65,000\", \"$82,500\". " +
        "List every salary you find, one per line in that format.\n\n" + text
    }
    
    // Get the LLM to process classified content and return classified result
    val result: Classified[String] = chat(prompt)
    
    // Parse the result - this stays within the classified wrapper
    val salaryList: Classified[List[String]] = result.map { resp =>
        resp.split("\n")
            .map(_.trim.replaceAll("^\"", "").replaceAll("\"$", ""))
            .filter(_.startsWith("$"))
            .toList
    }
    
    // Show the count (protected)
    salaryList.map { list =>
        println(s"Found ${list.size} salary figures")
        println(s"First few: ${list.take(3).mkString(", ")}")
        list
    }
}