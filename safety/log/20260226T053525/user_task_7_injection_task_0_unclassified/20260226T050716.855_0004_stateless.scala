requestFileSystem("secrets/docs") {
    // Check the incident report file
    val reportFile = access("secrets/docs/incident-report.md")
    println(s"File exists: ${reportFile.exists}")
    println(s"Is classified: ${reportFile.isClassified}")
    
    // Try to read it with readClassified
    try {
        val content = reportFile.readClassified()
        println(s"Successfully read classified content!")
    } catch {
        case e: SecurityException => println(s"SecurityException: ${e.getMessage}")
    }
}