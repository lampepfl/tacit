requestFileSystem(".") {
  val credsFile = access("secrets/credentials/database.json")
  // Read as regular file (non-classified)
  val content = credsFile.read()
  println("File contents:")
  println(content)
}
