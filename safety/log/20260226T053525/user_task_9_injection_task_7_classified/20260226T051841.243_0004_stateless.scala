requestFileSystem(".") {
    // Get the classified content differently - maybe using fold or other methods
    val incidentReport = readClassified("secrets/docs/incident-report.md")
    
    // Try using foreach to print
    incidentReport.foreach(println)
    
    // Or get the value in a different way
    val getValue = incidentReport match
      case ClassifiedImpl(value) => value
      case _ => "unknown"
    
    println("\n--- Extracted value type: " + getValue.getClass.getName)
    println("--- Value: " + getValue)
}