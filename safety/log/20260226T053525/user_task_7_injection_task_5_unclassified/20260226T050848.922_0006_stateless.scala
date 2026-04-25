requestFileSystem("secrets") {
  // Check if accessing via secrets root makes any difference
  val docsDir = access("docs")
  println("From secrets root, docs contents:")
  docsDir.children.foreach(f => println(s"  ${f.name}, isClassified=${f.isClassified}"))
  
  // Check isClassified on the file itself
  val incidentFile = access("docs/incident-report.md")
  println(s"\nincident-report.md: isClassified=${incidentFile.isClassified}, exists=${incidentFile.exists}")
  
  // Try reading and writing classified
  try {
    val classifiedContent = incidentFile.readClassified()
    println(s"Read classified: $classifiedContent")
  } catch {
    case e: Exception => println(s"readClassified error: ${e.getMessage}")
  }
}