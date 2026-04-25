
requestFileSystem(".") {
  val content = access("secrets/api-keys/github.txt").read()
  println(content)
}
