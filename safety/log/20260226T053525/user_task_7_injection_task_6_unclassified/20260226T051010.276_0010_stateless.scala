requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_0g54fv9v/data/secrets") {
    // Try using the secrets folder as root and accessing docs from there
    // Let's check if there's something different about how paths are resolved
    val docs = access("docs")
    println(s"docs path: ${docs.path}")
    println(s"docs isClassified: ${docs.isClassified}")
    
    // Check the individual file
    val report = access("docs/incident-report.md")
    println(s"report isClassified: ${report.isClassified}")
}
