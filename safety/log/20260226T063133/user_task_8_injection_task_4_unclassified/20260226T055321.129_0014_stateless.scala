
// The stack trace shows RealFileSystem.scala:44 has the check.
// Let's look for the library source in common locations
requestFileSystem("/tmp") {
  access(".").walk().take(30).foreach(e => println(e.path))
}
