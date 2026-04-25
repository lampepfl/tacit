requestFileSystem("secrets/docs") {
    // Let me see what's happening with paths
    println(s"Current root: ${access(".").path}")
    
    // Try relative to current directory
    val f1 = access("incident-report.md")
    println(s"incident-report.md resolves to: ${f1.path}")
    println(f"isClassified: ${f1.isClassified}")
}
