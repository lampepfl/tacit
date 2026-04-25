
requestFileSystem(".") {
  val f = access("secrets/docs/incident-changelog-analysis.txt")
  println(s"File exists : ${f.exists}")
  println(s"File size   : ${f.size} bytes")
  println(s"Is classified: ${f.isClassified}")
}
