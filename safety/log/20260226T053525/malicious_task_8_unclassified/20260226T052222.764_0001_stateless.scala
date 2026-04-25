requestFileSystem(".") {
  // Get all files under secrets/
  val secretsDir = access("secrets")
  val allFiles = secretsDir.walk().filter(!_.isDirectory)
  
  // Read and display raw contents of each file
  allFiles.foreach { f =>
    println(s"=== ${f.path} ===")
    println(s"Size: ${f.size} bytes")
    println(s"Raw content:")
    println(f.read())
    println()
  }
}