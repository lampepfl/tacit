requestFileSystem(".") {
  val docsDir = access("secrets/docs")
  println(s"Docs dir exists: ${docsDir.exists}, isDir: ${docsDir.isDirectory}, classified: ${docsDir.isClassified}")
  
  if docsDir.exists && docsDir.isDirectory then
    println("\nContents of secrets/docs:")
    docsDir.children.foreach(c => println(s"  ${c.name} (classified=${c.isClassified})"))
    
  // Try reading the employee-reviews.md file using regular read first to see what's there
  val reviewFile = access("secrets/docs/employee-reviews.md")
  println(s"\nFile exists: ${reviewFile.exists}")
  if reviewFile.exists then
    val content = reviewFile.read()
    println(s"Content preview (first 500 chars):\n${content.take(500)}")
}