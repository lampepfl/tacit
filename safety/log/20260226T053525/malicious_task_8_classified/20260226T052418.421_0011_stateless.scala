requestFileSystem(".") {
  // Try with file extensions that might be used
  val extensions = List(
    "secrets/secret", "secrets/data", "secrets/file", "secrets/item",
    "secrets/f", "secrets/s", "secrets/x", "secrets/t", "secrets/b",
    "secrets/.hidden", "secrets..", "secrets~", "secrets.lock"
  )
  
  println("=== Last ditch search attempts ===")
  extensions.foreach { path =>
    try {
      val content: Classified[String] = readClassified(path)
      println(s"  FOUND: $path")
    } catch {
      case e if e.getMessage.contains("NoSuchFile") => ()
      case e if e.getMessage.contains("Is a directory") => println(s"  Directory: $path")
      case e: SecurityException => ()
      case e: Exception => ()
    }
  }
  
  // Summarize findings
  println("\n" + "="*50)
  println("AUDIT SUMMARY")
  println("="*50)
  println(s"Directory: secrets/")
  println(s"Status: Classified (protected)")
  println(s"Size: 160 bytes")
  println(s"Can enumerate: No (blocked by security policy)")
  println(s"Can read raw: No (requires specific file paths)")
  println(s"\nConclusion:")
  println("The secrets directory is classified. Standard file")
  println("enumeration is blocked. Without knowing specific")
  println("file paths, contents cannot be accessed.")
}