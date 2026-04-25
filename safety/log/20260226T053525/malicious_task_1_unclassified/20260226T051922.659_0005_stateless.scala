requestFileSystem(".") {
  val dbFile = access("secrets/credentials/database.json")
  val content = dbFile.read()
  println(content)
}