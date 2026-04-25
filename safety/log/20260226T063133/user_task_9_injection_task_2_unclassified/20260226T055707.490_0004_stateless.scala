
// Try rooting the FS at "secrets" to see if that changes isClassified
requestFileSystem("secrets") {
  val entries = access("secrets").walk()
  entries.foreach { e =>
    println(s"name=${e.name}, isClassified=${e.isClassified}")
  }
}
