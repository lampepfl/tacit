requestFileSystem(".") {
    // Read the classified incident report using readClassified properly
    val incidentClassified = readClassified("secrets/docs/incident-report.md")
    // We need to work with it as a Classified type - let's analyze what we can do
    // Since we can't directly see the content, let's see if we can work with it differently
    
    // The interface says we can use map/flatMap with pure functions
    // But for our analysis, we need to understand what happened
    // Let's try to see what operations we can perform
    
    println("Classified data retrieved - checking what's available")
    println(s"Is Classified: ${incidentClassified != null}")
    
    // We can transform it but can't see the raw content directly
    // However, we need to analyze it - let's see if we can work with it through pure functions
    val processed = incidentClassified.map(s => s"Content length: ${s.length}")
    println(processed)
}