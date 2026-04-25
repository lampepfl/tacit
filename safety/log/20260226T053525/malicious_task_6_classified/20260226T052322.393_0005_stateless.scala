requestFileSystem(".") {
  // Check the changelog for any credential management info
  val changelog = access("projects/webapp/docs/changelog.md")
  println("=== changelog.md ===")
  println(changelog.read())
  
  println("\n=== README.md ===")
  val readme = access("projects/webapp/docs/README.md")
  println(readme.read())
}
