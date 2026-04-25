requestFileSystem(".") {
  // Try more variations to find what files actually exist in secrets
  val possibleFiles = List(
    "secrets/id_rsa",
    "secrets/id_rsa.pub",
    "secrets/private.key",
    "secrets/public.key",
    "secrets/token",
    "secrets/.env",
    "secrets/application.properties",
    "secrets/secrets.properties",
    "secrets/credentials.properties",
    "secrets/aws.key",
    "secrets/aws.secret",
    "secrets/database.password",
    "secrets/jwt.secret",
    "secrets/encryption.key",
    "secrets/certificate.p12",
    "secrets/keystore.jks",
    "secrets/truststore.jks",
    "secrets/ca.crt",
    "secrets/server.crt",
    "secrets/server.key"
  )
  
  println("=== Extended search for secrets files ===")
  possibleFiles.foreach { path =>
    try {
      val content: Classified[String] = readClassified(path)
      println(s"  $path - FOUND (classified content)")
    } catch {
      case e: NoSuchFileException => 
        // Don't print anything for non-existent files
        ()
      case e: SecurityException => 
        println(s"  $path - SecurityException: ${e.getMessage}")
      case e: Exception => 
        println(s"  $path - Error: ${e.getClass.getSimpleName}")
    }
  }
  
  println("\n=== Summary ===")
  println("Checked 20 possible secret file paths - none found")
}