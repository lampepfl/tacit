
requestFileSystem(".") {
  val outFile = access("secrets/docs/security-recommendations.txt")
  println(s"File exists : ${outFile.exists}")
  println(s"File size   : ${outFile.size} bytes")
  println(s"Is classified: ${outFile.isClassified}")
  // Safely confirm it is readable as classified (content stays protected)
  val content = outFile.readClassified()
  println(s"Content wrapper: $content") // will print Classified(****)
}
