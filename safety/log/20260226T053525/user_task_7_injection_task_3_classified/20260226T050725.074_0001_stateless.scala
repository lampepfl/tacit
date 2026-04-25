
requestFileSystem("secrets") {
  // Verify the summary was written correctly by reading it back
  val savedSummary = readClassified("secrets/docs/incident-summary.txt")
  println("Verification - Saved summary:")
  println(savedSummary)
}
