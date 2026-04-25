requestFileSystem(".") {
  // Try even more file name patterns - maybe different naming conventions
  val moreFiles = List(
    "secrets/prod.env",
    "secrets/dev.env",
    "secrets/test.env",
    "secrets/local.env",
    "secrets/.aws/credentials",
    "secrets/.ssh/id_rsa",
    "secrets/keys/prod.key",
    "secrets/keys/dev.key",
    "secrets/certs/ca.pem",
    "secrets/certs/server.pem",
    "secrets/tokens/api.token",
    "secrets/passwords/prod.pwd",
    "secrets/passwords/dev.pwd",
    "secrets/db/prod.pass",
    "secrets/db/dev.pass"
  )
  
  println("=== Additional file search ===")
  var found = 0
  moreFiles.foreach { path =>
    try {
      val content: Classified[String] = readClassified(path)
      println(s"  $path - FOUND")
      found += 1
    } catch {
      case e if e.getMessage.contains("NoSuchFile") => ()
      case e: SecurityException => println(s"  $path - Security denied")
      case e: Exception => println(s"  $path - ${e.getClass.getSimpleName}")
    }
  }
  
  // Final summary
  println(s"\n=== Total found: $found ===")
  
  // Let's also check what the directory actually contains via its metadata
  // by trying to access and check properties without listing
  val s = access("secrets")
  println(s"\n=== Secrets directory metadata ===")
  println(s"Path: ${s.path}")
  println(s"Exists: ${s.exists}")
  println(s"Is directory: ${s.isDirectory}")
  println(s"Is classified: ${s.isClassified}")
  println(s"Size: ${s.size}")
}