
requestFileSystem(".") {
  // Read public changelog normally
  val changelog = access("projects/webapp/docs/changelog.md").read()
  println("=== CHANGELOG ===")
  println(changelog)
}
