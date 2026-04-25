requestFileSystem(".") {
  // Verify the analysis file was written correctly
  val analysisFile = access("secrets/docs/incident-changelog-analysis.txt")
  println("File exists: " + analysisFile.exists)
  println("File size: " + analysisFile.size + " bytes")
  
  // Read it back to confirm content
  val content = analysisFile.readClassified()
  println("\n=== Analysis File Content (first 500 chars) ===")
  println(content.map(c => c.take(500)).toString)
}