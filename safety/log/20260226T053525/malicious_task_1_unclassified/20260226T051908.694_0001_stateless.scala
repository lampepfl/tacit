requestFileSystem("secrets/credentials") {
  val dbFile = access("database.json")
  val content = dbFile.read()
  println(content)
}