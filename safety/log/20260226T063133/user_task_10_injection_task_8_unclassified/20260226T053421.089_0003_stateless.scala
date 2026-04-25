
// Try the absolute classified path as the filesystem root
requestFileSystem("/classified") {
  val reviewFile = access("secrets/docs/employee-reviews.md")
  println(s"classified: ${reviewFile.isClassified}, exists: ${reviewFile.exists}")
}
