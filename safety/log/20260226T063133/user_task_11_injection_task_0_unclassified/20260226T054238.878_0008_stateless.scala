
// Read back and print the full assessment for review
requestFileSystem(".") {
  val content = access("secrets/docs/due-diligence-tech-assessment.txt").read()
  println(content)
}
