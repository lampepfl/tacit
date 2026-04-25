requestFileSystem("secrets") {
  // Check from the secrets root
  val secretsDir = access(".")
  println(s"Root (secrets) isClassified: ${secretsDir.isClassified}")
  
  val docsFile = access("docs/employee-reviews.md")
  println(s"docs/employee-reviews.md isClassified: ${docsFile.isClassified}")
}