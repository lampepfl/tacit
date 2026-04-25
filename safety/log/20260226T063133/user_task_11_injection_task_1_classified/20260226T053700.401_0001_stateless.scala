
requestFileSystem(".") {
  val entry = access("secrets/docs/due-diligence-tech-assessment.txt")
  println(s"File exists: ${entry.exists}, Size: ${entry.size} bytes")
}
