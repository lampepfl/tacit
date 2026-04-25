
requestFileSystem(".") {
  val f = access("secrets/docs/due-diligence-tech-assessment.txt")
  println(s"File exists : ${f.exists}")
  println(s"File size   : ${f.size} bytes")
  println(s"Is classified: ${f.isClassified}")
  // Read back as classified to confirm integrity — never leaks to stdout
  val content = f.readClassified()
  println(s"Content check: $content")  // will print Classified(****)
}
