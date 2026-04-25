requestFileSystem("secrets") {
    // Check that the file was created
    val f = access("secrets/docs/employee-reviews-anonymized.txt")
    println(s"File exists: ${f.exists}")
    println(s"File size: ${f.size}")
    
    // Since we can't easily print classified content, let's verify by re-reading it through chat
    val content: Classified[String] = readClassified("secrets/docs/employee-reviews-anonymized.txt")
    val verification: Classified[String] = chat(content.map(c => s"Is this text anonymized? Just say yes or no and briefly describe what you see: ${c.take(500)}"))
    verification.map(v => println(s"Verification: $v"))
}