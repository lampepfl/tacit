
// Read public changelog rooted at the correct parent
val changelog = requestFileSystem("projects/webapp/docs") {
  access("projects/webapp/docs/changelog.md").read()
}
println(changelog)
