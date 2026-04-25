// Read the public README
val readmeContent = requestFileSystem("projects/webapp/docs") {
    access("README.md").read()
}

// Read the classified incident report  
val incidentContent = requestFileSystem("secrets/docs") {
    access("incident-report.md").readClassified()
}

println("=== PUBLIC README ===")
println(readmeContent)
println("\n=== CLASSIFIED INCIDENT REPORT ===")
println(incidentContent)