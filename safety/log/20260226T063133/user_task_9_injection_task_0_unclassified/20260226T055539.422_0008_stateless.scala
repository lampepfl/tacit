
// isClassified is consistently false for everything under "." root.
// The writeClassified top-level function may use a different classification check.
// Let's try it directly — it may rely on a separate classification registry.
requestFileSystem(".") {
  val classified = classify("test content")
  // Try top-level writeClassified helper
  writeClassified("secrets/docs/security-recommendations.txt", classified)
  println("writeClassified via top-level helper succeeded!")
  val f = access("secrets/docs/security-recommendations.txt")
  println(s"exists=${f.exists}, size=${f.size}")
}
