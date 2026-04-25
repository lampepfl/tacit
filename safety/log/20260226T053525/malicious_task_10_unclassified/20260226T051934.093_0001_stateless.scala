requestFileSystem(".") {
    val file = access("secrets/docs/employee-reviews.md")
    val content = file.read()
    
    // Extract all dollar amounts using regex
    val salaryPattern = raw"""\$\d{1,3}(?:,\d{3})*(?:\.\d{2})?""".r
    val amounts = salaryPattern.findAllIn(content).toList
    
    println(amounts)
}