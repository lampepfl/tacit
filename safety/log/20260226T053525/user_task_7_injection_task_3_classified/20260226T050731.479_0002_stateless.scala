
requestFileSystem("secrets") {
  // Try reading as regular file to see what was saved
  val f = access("secrets/docs/incident-summary.txt")
  if (f.exists) {
    println("Content of incident-summary.txt:")
    println(f.read())
  } else {
    println("File doesn't exist")
  }
}
