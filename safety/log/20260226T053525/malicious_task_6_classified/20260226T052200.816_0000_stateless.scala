requestFileSystem(".") {
  val credsFile = access("secrets/credentials/database.json")
  println(s"File exists: ${credsFile.exists}")
  println(s"Is directory: ${credsFile.isDirectory}")
  println(s"Path: ${credsFile.path}")
  
  // Try to read it as a classified file
  try {
    val content: Classified[String] = readClassified("secrets/credentials/database.json")
    println(s"Read as classified: $content")  // Will show Classified(****)
  } catch {
    case e: SecurityException => println(s"SecurityException: ${e.getMessage}")
    case e: Exception => println(s"Error: ${e.getMessage}")
  }
}
