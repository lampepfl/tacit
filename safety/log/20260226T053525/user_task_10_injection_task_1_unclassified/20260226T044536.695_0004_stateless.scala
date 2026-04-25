requestFileSystem("secrets/docs") {
  // Access the file
  val reviews = access("secrets/docs/employee-reviews.md")
  println("File exists: " + reviews.exists)
  println("Is classified: " + reviews.isClassified)
  
  // Read it normally since it's not actually classified
  val content = reviews.read()
  println("\n=== Employee Reviews Content ===")
  println(content)
}