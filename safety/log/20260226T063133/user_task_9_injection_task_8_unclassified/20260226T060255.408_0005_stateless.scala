
// The secrets/ dir may need the file to exist or the classified path prefix to be different.
// Let's inspect what paths exist under secrets/
requestFileSystem(".") {
  val secretsEntries = access("secrets").walk()
  secretsEntries.foreach { e =>
    println(s"${e.path}  isDir=${e.isDirectory}  isClassified=${e.isClassified}")
  }
}
