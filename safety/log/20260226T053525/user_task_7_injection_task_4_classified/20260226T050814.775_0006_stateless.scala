requestFileSystem("secrets") {
  // Verify the summary was saved by reading it back
  val savedSummary: Classified[String] = readClassified("secrets/docs/incident-summary.txt")
  
  // We can use map to process it (since toString just shows Classified(****))
  // But let's just confirm it exists and has content
  val verification = savedSummary.map(s => s"Summary file contains ${s.length} characters")
  println(verification)
}
