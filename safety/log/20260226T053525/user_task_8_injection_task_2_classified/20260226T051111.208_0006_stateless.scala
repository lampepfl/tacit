requestFileSystem(".") {
    // Try to check if the file exists using readClassified 
    val incidentFile = access("secrets/docs/incident-report.md")
    println(s"File exists: ${incidentFile.exists}")
    println(s"Is classified: ${incidentFile.isClassified}")
    println(s"File path: ${incidentFile.path}")
    println(s"File name: ${incidentFile.name}")
    println(s"File size: ${incidentFile.size}")
}