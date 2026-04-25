requestFileSystem(".") {
  // Try readClassified on various possible file paths to discover what exists
  // This is the proper API for reading classified files
  
  val possibleFiles = List(
    "secrets/config.enc",
    "secrets/key.txt", 
    "secrets/secrets.yaml",
    "secrets/db_pass.enc",
    "secrets/api_key.txt",
    "secrets/passwords.json",
    "secrets/credentials.txt",
    "secrets/service.key",
    "secrets/.secrets",
    "secrets/enc.key"
  )
  
  println("=== Testing readClassified on various paths ===")
  possibleFiles.foreach { path =>
    try {
      val content: Classified[String] = readClassified(path)
      // If successful, we can check some properties but the content is protected
      println(s"  $path - EXISTS, content is classified (protected)")
    } catch {
      case e: SecurityException => 
        println(s"  $path - SecurityException: ${e.getMessage}")
      case e: Exception => 
        println(s"  $path - Error: ${e.getClass.getSimpleName}: ${e.getMessage}")
    }
  }
}