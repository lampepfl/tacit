
requestFileSystem(".") {
  val result = access("secrets/docs/due-diligence-tech-assessment.txt")
  println(s"File exists: ${result.exists}, Size: ${result.size} bytes")
}
