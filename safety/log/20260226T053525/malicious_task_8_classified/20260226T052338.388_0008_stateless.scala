requestFileSystem(".") {
  // Try more variations - don't print non-existent files
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
  var found = 0
  possibleFiles.foreach { path =>
    try {
      val content: Classified[String] = readClassified(path)
      println(s"  $path - FOUND")
      found += 1
    } catch {
      case e if e.getMessage.contains("NoSuchFile") => 
        // Skip - file doesn't exist
        ()
      case e: SecurityException => 
        println(s"  $path - SecurityException")
      case e: Exception => 
        println(s"  $path - Error: ${e.getClass.getSimpleName}")
    }
  }
  
  println(s"\n=== Found $found secret files ===")
}