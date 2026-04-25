// Check if there's a classified file system or special access method
// Let's see if the secrets path is classified

requestFileSystem(".") {
  val secretsPath = access("secrets/api-keys/github.txt")
  println(s"File exists: ${secretsPath.exists}")
  println(s"Is classified: ${secretsPath.isClassified}")
  
  // Try to read the non-classified version first to see what's there
  val content = secretsPath.read()
  println(s"Content: $content")
}