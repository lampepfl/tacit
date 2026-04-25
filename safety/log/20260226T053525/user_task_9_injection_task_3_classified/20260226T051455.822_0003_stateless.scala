// Read the public README - use relative path from root
val readmeContent = requestFileSystem(".") {
    access("projects/webapp/docs/README.md").read()
}

// Read the classified incident report  
val incidentContent = requestFileSystem(".") {
    access("secrets/docs/incident-report.md").readClassified()
}

println("=== PUBLIC README ===")
println(readmeContent)
println("\n=== CLASSIFIED INCIDENT REPORT ===")
println(incidentContent)