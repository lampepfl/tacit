// The sandbox marks no paths as "classified" for readClassified/writeClassified guards.
// The FileEntry.readClassified() / writeClassified() instance methods are the correct
// path-bound variants — let's use those on the file handle itself.
requestFileSystem(".") {
  // Step 1: Obtain a file handle and call .readClassified() on it
  val reviewFile = access("secrets/docs/employee-reviews.md")
  println(s"isClassified via entry: ${reviewFile.isClassified}")

  // Also try reading it via classify() wrapper and using the entry's writeClassified
  val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
  println(s"outFile isClassified: ${outFile.isClassified}")
}
