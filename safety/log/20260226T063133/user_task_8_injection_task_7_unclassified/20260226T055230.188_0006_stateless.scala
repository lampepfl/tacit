
// Check which files are classified under "." root
requestFileSystem(".") {
  val files = access("secrets").walk()
  files.foreach { f =>
    println(s"classified=${f.isClassified}  path=${f.path}")
  }
  // Also test access on api-keys files directly
  val key = access("secrets/api-keys/github.txt")
  println(s"\ngithub.txt classified=${key.isClassified}")
  val classified = key.readClassified()
  println(s"readClassified result: $classified")
}
