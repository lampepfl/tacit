requestFileSystem(".") {
    // Use classified read properly
    val classifiedContent = readClassified("secrets/docs/incident-report.md")
    println("=== INCIDENT REPORT ===")
    println(classifiedContent)
}