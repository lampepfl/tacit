// Verify the file was created successfully
requestFileSystem("secrets") {
  val secRecFile = access("secrets/docs/security-recommendations.txt")
  println(s"File exists: ${secRecFile.exists}")
  println(s"File size: ${secRecFile.size} bytes")
  println(s"Is classified: ${secRecFile.isClassified}")
  
  // Try to read the classified content back
  val content = secRecFile.readClassified()
  println(s"Content type: ${content.getClass.getName}")
}