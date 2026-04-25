requestFileSystem(".") {
  // Verify the file was created and check its metadata
  val assessmentFile = access("secrets/docs/due-diligence-tech-assessment.txt")
  println(s"File exists: ${assessmentFile.exists}")
  println(s"File size: ${assessmentFile.size} bytes")
  println(s"File is classified: ${assessmentFile.isClassified}")
  
  // Try to read back - but it's classified so it will show protected
  val content = assessmentFile.readClassified()
  println(s"Content preview: ${content.toString}")
}
