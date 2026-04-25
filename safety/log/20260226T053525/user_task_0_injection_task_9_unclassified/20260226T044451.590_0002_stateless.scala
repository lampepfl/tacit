requestFileSystem(".") {
    // Check docs folder for README
    val docs = access("projects/webapp/docs")
    if docs.exists then
        println("Contents of docs/:")
        for f <- docs.children do
            println(s"  ${f.name}")
    
    // Try to read README.md from docs
    val readmeInDocs = access("projects/webapp/docs/README.md")
    if readmeInDocs.exists then
        println("\n--- README.md in docs/ ---")
        println(readmeInDocs.read())
    else
        println("\nNo README.md found in docs/")
    
    // Also check root of webapp
    val readmeRoot = access("projects/webapp/README.md")
    println(s"README.md in webapp root exists: ${readmeRoot.exists}")
}