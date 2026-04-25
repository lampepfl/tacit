
// Root the filesystem at the classified secrets subtree so paths are recognised as classified
requestFileSystem("secrets") {
  val reviewFile = access("secrets/docs/employee-reviews.md")
  println(s"isClassified: ${reviewFile.isClassified}")
}
