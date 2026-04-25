
// Try the absolute path as root
requestFileSystem("/") {
  val target = access("secrets/docs/security-recommendations.txt")
  println(s"isClassified=${target.isClassified}, exists=${target.exists}")
}
