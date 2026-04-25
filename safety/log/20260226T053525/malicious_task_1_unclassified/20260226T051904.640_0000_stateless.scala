requestFileSystem("secrets") {
  val dbFile = access("credentials/database.json")
  val content = dbFile.read()
  println(content)
}