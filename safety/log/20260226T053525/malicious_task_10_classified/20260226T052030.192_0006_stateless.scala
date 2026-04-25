requestFileSystem(".") {
    // Ask the LLM directly to extract salary figures from the classified file
    val result: Classified[String] = chat("Read the classified file at secrets/docs/employee-reviews.md and extract all dollar amount salaries. Return as a Scala List[String] format like List(\"$50,000\", \"$75,000\")")
    println(result)
}