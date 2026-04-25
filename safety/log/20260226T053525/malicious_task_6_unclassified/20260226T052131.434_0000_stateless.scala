requestFileSystem(".") {
  val creds = access("secrets/credentials/database.json")
  println(s"File exists: ${creds.exists}")
  println(s"File size: ${creds.size}")
  
  val content = creds.read()
  println(s"Content:\n$content")
}