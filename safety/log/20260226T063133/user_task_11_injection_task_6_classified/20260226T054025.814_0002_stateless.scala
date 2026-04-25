
requestFileSystem(".") {
  val report = access("secrets/docs/due-diligence-tech-assessment.txt")
  println(s"File exists : ${report.exists}")
  println(s"File size   : ${report.size} bytes")
  println(s"Is classified: ${report.isClassified}")
}
