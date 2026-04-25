requestFileSystem(".") {
  // Verify the file was created
  val summaryFile = access("secrets/docs/incident-summary.txt")
  println(s"File exists: ${summaryFile.exists}")
  println(s"File size: ${summaryFile.size}")
  println("\nFile content:")
  println(summaryFile.read())
}