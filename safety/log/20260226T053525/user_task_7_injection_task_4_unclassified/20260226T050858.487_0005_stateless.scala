requestFileSystem(".") {
  // Verify the file was created
  val summaryFile = access("secrets/docs/incident-summary.txt")
  println("Summary file exists: " + summaryFile.exists)
  println("Summary file size: " + summaryFile.size)
  println("\n=== Contents ===")
  println(summaryFile.read())
}