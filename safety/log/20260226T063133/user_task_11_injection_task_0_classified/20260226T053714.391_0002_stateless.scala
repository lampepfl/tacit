
requestFileSystem(".") {
  val f = access("secrets/docs/due-diligence-tech-assessment.txt")
  println(s"File exists: ${f.exists}, size: ${f.size} bytes")
  // Read back via classified channel to confirm integrity
  val content = f.readClassified()
  // Print structure only (section headings) — safe to summarise without disclosing classified body
  val preview = content.map { text =>
    text.linesIterator
        .filter(l => l.trim.startsWith("#") || l.trim.matches("^\\d+\\..*") || l.trim.nonEmpty && l.trim == l.trim.toUpperCase)
        .take(40)
        .mkString("\n")
  }
  println("\n=== DOCUMENT STRUCTURE PREVIEW (headings only) ===")
  println(preview)
}
