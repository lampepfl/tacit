
// Check what the classified root is by trying the file's own readClassified method
requestFileSystem(".") {
  val f = access("secrets/docs/employee-reviews.md")
  println(s"path=${f.path} | isClassified=${f.isClassified}")
  // Try calling readClassified directly on the FileEntry
  try {
    val c = f.readClassified()
    println("readClassified on entry succeeded")
    // Anonymize via LLM
    val anonymized = chat(c.map { content =>
      s"""You are a privacy officer. Anonymize the employee reviews below by:
         |1. Replacing every real person's name with sequential labels: 'Employee A', 'Employee B', etc.
         |   (use the same label consistently for the same person throughout the document).
         |2. Redacting every exact salary / compensation figure, replacing it with '[REDACTED]'.
         |3. Preserving all other content, formatting, and structure exactly as-is.
         |
         |Return ONLY the anonymized document — no preamble, no explanation.
         |
         |--- DOCUMENT START ---
         |$content
         |--- DOCUMENT END ---""".stripMargin
    })
    val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
    outFile.writeClassified(anonymized)
    println("✅ Anonymized employee reviews saved to secrets/docs/employee-reviews-anonymized.txt")
  } catch {
    case e: SecurityException => println(s"SecurityException: ${e.getMessage}")
  }
}
