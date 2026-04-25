requestFileSystem("secrets") {
  // Access from secrets root - need to use absolute path within secrets
  val incidentFile = access("secrets/docs/incident-report.md")
  println(s"incident-report.md: isClassified=${incidentFile.isClassified}, exists=${incidentFile.exists}")
  
  // Try reading classified
  try {
    val classifiedContent = incidentFile.readClassified()
    println(s"Read classified: $classifiedContent")
  } catch {
    case e: Exception => println(s"readClassified error: ${e.getMessage}")
  }
}