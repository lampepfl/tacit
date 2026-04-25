requestFileSystem(".") {
  // Check the classified status of files under secrets/
  val secretsDir = access("secrets")
  
  println("=== Checking secrets directory classification status ===")
  println(s"Secrets path is classified: ${secretsDir.isClassified}")
  
  // The walk() method should work to list files, just not their contents via regular read
  val allFiles = secretsDir.walk()
  println(s"\nTotal files found: ${allFiles.length}")
  
  // List each file with its metadata
  allFiles.foreach { f =>
    println(s"  - ${f.name}: isClassified=${f.isClassified}, size=${f.size}")
  }
}