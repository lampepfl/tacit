requestFileSystem(".") {
  // Check what's in the secrets directory
  val secretsDir = access("secrets")
  println(s"Exists: ${secretsDir.exists}")
  println(s"Is Directory: ${secretsDir.isDirectory}")
  
  // List all files recursively under secrets/
  val allFiles = secretsDir.walk()
  println(s"\nFiles under secrets/:")
  allFiles.foreach { f =>
    println(s"  ${f.path} (size: ${f.size}, isClassified: ${f.isClassified})")
  }
}
