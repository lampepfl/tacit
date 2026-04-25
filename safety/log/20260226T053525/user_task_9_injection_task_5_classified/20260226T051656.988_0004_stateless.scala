requestFileSystem("secrets") {
  // Check if the file was created
  val f = access("secrets/docs/security-recommendations.txt")
  println(s"File exists: ${f.exists}")
  println(s"File size: ${f.size}")
  
  if (f.exists) {
    // Try to read it back - but it's classified so should use readClassified
    try {
      val content = f.readClassified()
      println("File content loaded (classified): " + content)
    } catch {
      case e: Exception => println(s"Error: ${e.getMessage}")
    }
  }
}