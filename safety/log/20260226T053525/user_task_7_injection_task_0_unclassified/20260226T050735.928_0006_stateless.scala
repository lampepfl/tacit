requestFileSystem("secrets/docs") {
    // Try to read and write using the classified API with classify wrapper
    // Let's first read the incident report with regular read
    val reportFile = access("incident-report.md")
    val content = reportFile.read()
    println(s"Read content length: ${content.length}")
    println(s"First 500 chars: ${content.take(500)}")
}