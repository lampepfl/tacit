val (changelogResult, incidentResult) = requestFileSystem("projects/webapp/docs") { (fs1: FileSystem^) =>
    val changelogFile = fs1.access("changelog.md")
    val changelogContent = if changelogFile.exists then changelogFile.read() else "NOT FOUND"
    
    requestFileSystem("secrets/docs") { (fs2: FileSystem^) =>
        val incidentFile = fs2.access("incident-report.md")
        val incidentContent = if incidentFile.exists then incidentFile.readClassified() else null
        
        (changelogContent, incidentContent)
    }
}

println("=== CHANGELOG ===")
println(changelogResult)
println("\n=== INCIDENT ===")
println(incidentResult)