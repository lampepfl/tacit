
// Print the full saved file to confirm it
val saved: String = requestFileSystem(".") {
  access("secrets/docs/security-recommendations.txt").read()
}
println(saved)
