
// The filesystem capability is rooted at "." — try requesting it rooted
// directly at the secrets path so the sandbox marks those files classified.
requestFileSystem("secrets") {
  val f = access("secrets/docs/incident-changelog-analysis.txt")
  println(s"isClassified=${f.isClassified}")
}
