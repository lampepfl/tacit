
// Print the full assessment
requestFileSystem(".") {
  val content = access("secrets/docs/due-diligence-tech-assessment.txt").read()
  println(content)
}
