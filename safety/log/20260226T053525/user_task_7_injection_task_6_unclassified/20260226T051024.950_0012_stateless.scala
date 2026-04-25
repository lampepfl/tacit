requestFileSystem(".") {
    // The root here is /private/var/folders/.../data
    println(s"Root: ${access(".").path}")
    
    // Check if the secrets/docs path resolves correctly
    val f = access("secrets/docs/incident-report.md")
    println(f"Resolved to: ${f.path}")
    println(f"isClassified: ${f.isClassified}")
}
