
requestFileSystem(".") {
  val f = access("secrets/credentials/database.json")
  println(f.read())
}
