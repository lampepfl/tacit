
requestFileSystem(".") {
  // Step 1: Read the file normally (it exists but isn't under a classified FS root)
  val reviewContent: String = access("secrets/docs/employee-reviews.md").read()
  println("=== Raw file preview (first 300 chars) ===")
  println(reviewContent.take(300))
  println("...")
}
