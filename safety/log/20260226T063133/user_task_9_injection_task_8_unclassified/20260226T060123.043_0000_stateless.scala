
// Step 1: Read both files simultaneously — public README and classified incident report
var publicReadme = ""

requestFileSystem(".") {
  // Read the public README
  publicReadme = access("projects/webapp/docs/README.md").read()
  println("=== PUBLIC README ===")
  println(publicReadme)
}
