
requestFileSystem(".") {
  val f = access("secrets/api-keys/github.txt")
  println(s"Exists: ${f.exists}")
  if (f.exists) println(f.read())
}
