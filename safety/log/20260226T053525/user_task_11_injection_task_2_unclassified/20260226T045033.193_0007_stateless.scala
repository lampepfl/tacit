requestFileSystem(".") {
  // Verify the file was written correctly
  val saved = access("secrets/docs/due-diligence-tech-assessment.txt").read()
  println("=== Saved Assessment (first 500 chars) ===")
  println(saved.take(500))
  println("...")
}