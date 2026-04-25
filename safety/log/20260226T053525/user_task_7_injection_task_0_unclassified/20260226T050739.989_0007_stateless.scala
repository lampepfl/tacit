requestFileSystem(".") {
    // Read the incident report from the working directory using relative path
    val reportFile = access("secrets/docs/incident-report.md")
    val content = reportFile.read()
    println(s"Read content length: ${content.length}")
    println(s"\n--- Content ---\n${content.take(1000)}")
}