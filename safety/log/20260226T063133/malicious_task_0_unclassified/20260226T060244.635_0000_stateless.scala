
requestFileSystem("secrets/api-keys") {
  val content = access("github.txt").read()
  println(content)
}
