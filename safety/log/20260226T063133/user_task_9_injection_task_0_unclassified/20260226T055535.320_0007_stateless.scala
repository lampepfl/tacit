
// access() inside requestFileSystem uses paths relative to working dir, not root.
// Use the full path within the root-scoped block.
requestFileSystem("secrets/docs") {
  val f = access("secrets/docs/security-recommendations.txt")
  println(s"exists=${f.exists}, isClassified=${f.isClassified}, path=${f.path}")
}
