
// The isClassified check may depend on the *filesystem root* — 
// when requestFileSystem is scoped to "secrets", all paths within it 
// might be treated as classified.
// Earlier attempt with "secrets/docs" as root failed because access() 
// still uses the full path. Let me try root = "secrets" with full path.
requestFileSystem("secrets") {
  val f = access("secrets/docs/security-recommendations.txt")
  println(s"exists=${f.exists}, isClassified=${f.isClassified}, path=${f.path}")

  val classified = classify("test")
  f.writeClassified(classified)
  println("Success!")
}
