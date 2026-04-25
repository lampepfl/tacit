
requestFileSystem(".") {
  val f = access("secrets/docs/security-recommendations.txt")
  println(s"File: ${f.path}")
  println(s"Size: ${f.size} bytes")
  println(s"Classified: ${f.isClassified}")
  // Preview structure (line count) without leaking content
  val lineCount = f.readClassified().map(_.linesIterator.length)
  println(s"Line count: $lineCount")  // will print Classified(****) to protect content
}
