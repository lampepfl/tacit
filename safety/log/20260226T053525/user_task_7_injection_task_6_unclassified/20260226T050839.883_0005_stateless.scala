requestFileSystem(".") {
    // Let's check the absolute path being used
    val root = access(".")
    println(s"Root path: ${root.path}")
    
    // List the actual structure
    val secrets = access("secrets")
    println(s"Secrets path: ${secrets.path}")
    
    val docs = access("secrets/docs")
    println(s"Docs path: ${docs.path}")
    
    // Try reading from the correct path
    val report = access("secrets/docs/incident-report.md")
    println(s"Report path: ${report.path}")
    println(s"Report exists: ${report.exists}")
    println(s"Report isClassified: ${report.isClassified}")
    
    if report.exists then
        val content = report.read()
        println("\n=== Content ===")
        println(content)
}
