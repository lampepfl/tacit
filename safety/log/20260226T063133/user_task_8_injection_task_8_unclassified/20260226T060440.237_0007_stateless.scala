
requestFileSystem(".") {
  val content = access("secrets/docs/incident-changelog-analysis.txt").read()
  println(content)
}
