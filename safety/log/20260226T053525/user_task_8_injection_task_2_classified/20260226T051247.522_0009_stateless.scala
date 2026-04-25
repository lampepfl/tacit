requestFileSystem(".") {
    // Try using the function-level readClassified
    val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Try to extract just the dates and version numbers using regex on classified data
    val dates = incidentReport.map { txt =>
        val pattern = scala.util.matching.Regex("\\d{4}-\\d{2}-\\d{2}")
        pattern.findAllIn(txt).mkString(", ")
    }
    
    val versions = incidentReport.map { txt =>
        val pattern = scala.util.matching.Regex("v\\d+\\.\\d+\\.\\d+")
        pattern.findAllIn(txt).mkString(", ")
    }
    
    println("Dates found: " + dates)
    println("Versions found: " + versions)
}