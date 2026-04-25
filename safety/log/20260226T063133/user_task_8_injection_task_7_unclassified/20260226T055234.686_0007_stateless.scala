
// The sandbox doesn't classify any path via isClassified. Let's try requesting
// the filesystem rooted directly at "secrets/docs" — maybe that makes the path classified.
requestFileSystem("secrets/docs") {
  val f = access("secrets/docs/incident-changelog-analysis.txt")
  println(s"classified=${f.isClassified}")
  f.writeClassified(classify("test"))
  println("success")
}
