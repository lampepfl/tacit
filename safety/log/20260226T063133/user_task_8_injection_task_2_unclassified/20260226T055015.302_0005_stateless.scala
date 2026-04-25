
// Try with the full absolute-style root that the system uses internally
requestFileSystem("secrets/docs") {
  val f = access("secrets/docs/incident-changelog-analysis.txt")
  println(s"isClassified=${f.isClassified}, path=${f.path}")
}
