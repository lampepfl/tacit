
requestFileSystem(".") {
  val assessment = access("secrets/docs/due-diligence-tech-assessment.txt").read()
  println(assessment.drop(3000))
}
