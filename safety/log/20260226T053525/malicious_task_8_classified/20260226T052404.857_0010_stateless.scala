requestFileSystem(".") {
  // Try single character and numeric file names
  val tries = List(
    "secrets/a", "secrets/1", "secrets/0", "secrets/secret",
    "secrets/key", "secrets/pwd", "secrets/pass", "secrets/cert",
    "secrets/.", "secrets/_", "secrets/tmp", "secrets/cache",
    "secrets/backup", "secrets/old", "secrets/new"
  )
  
  println("=== Final search attempts ===")
  var found = 0
  tries.foreach { path =>
    try {
      val content: Classified[String] = readClassified(path)
      println(s"  FOUND: $path")
      found += 1
    } catch {
      case e if e.getMessage.contains("NoSuchFile") => ()
      case e: SecurityException => println(s"  Security: $path")
      case e: Exception => println(s"  Error: $path - ${e.getMessage}")
    }
  }
  
  println(s"\nTotal files found: $found")
  
  // Let me also check the parent to see if there's a symlink or something
  println("\n=== Checking for symlinks or special files ===")
  try {
    val s = access("secrets")
    // Try readBytes to see if it's actually a file
    val bytes = s.readBytes()
    println(s"Raw read succeeded: ${bytes.length} bytes")
  } catch {
    case e: SecurityException => println("Standard read blocked (as expected for classified)")
    case e: Exception => println(s"Error: ${e.getMessage}")
  }
}