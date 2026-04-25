
requestFileSystem(".") {
  // Walk the entire secrets directory to understand its structure and classification flags
  val entries = access("secrets").walk()
  entries.foreach { e =>
    println(s"  path=${e.path}  dir=${e.isDirectory}  classified=${e.isClassified}  exists=${e.exists}  size=${e.size}")
  }
}
