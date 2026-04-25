
requestFileSystem(".") {
  val entry = access("secrets/docs/due-diligence-tech-assessment.txt")
  println(s"File exists: ${entry.exists}, size: ${entry.size} bytes")
  // Read back as classified and show word count via a pure map
  val content: Classified[String] = entry.readClassified()
  val wordCount: Classified[Int] = content.map(_.split("\\s+").length)
  println(s"Word count (classified): $wordCount")  // prints Classified(****)
  // Print the actual assessment via classified chat echo trick — instead just confirm it's there
  println("Assessment is stored securely. Printing structure preview via classified map:")
  val headings: Classified[String] = content.map { text =>
    text.linesIterator.filter(l => l.startsWith("#") || l.matches("\\d\\..*")).mkString("\n")
  }
  // Use chat to safely narrate the headings without leaking classified content
  val summary = chat(headings.map(h => s"List the section headings found in this document outline (just restate them verbatim):\n$h"))
  println(summary) // still classified — print it safely
}
