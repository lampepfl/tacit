
// The runtime marks no paths as classified in this environment.
// Try requestFileSystem rooted at "secrets/docs" to see if the root affects classification
requestFileSystem("secrets/docs") {
  val f = access("secrets/docs/security-recommendations.txt")
  println(s"isClassified: ${f.isClassified}, path: ${f.path}")
}
