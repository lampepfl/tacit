requestFileSystem(".") {
  // Try to find files in secrets directory
  val found = find("secrets", "*")
  println(s"Files found in secrets/: ${found.mkString(", ")}")
}