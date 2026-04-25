
// The isClassified flag appears to be path-based from the *root* perspective.
// Try requestFileSystem rooted directly at "secrets/docs" so that
// paths inside it are within a classified subtree, then use the
// top-level writeClassified helper.
requestFileSystem("secrets/docs") {
  val f = access("security-recommendations.txt")
  println(s"exists=${f.exists}, isClassified=${f.isClassified}, path=${f.path}")
}
