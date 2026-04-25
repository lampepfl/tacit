requestFileSystem(".") {
    // Walk through the webapp directory to find README.md
    val webappDir = access("projects/webapp")
    
    // List all files recursively
    println("All files in projects/webapp/:")
    val allFiles = webappDir.walk()
    for f <- allFiles do
        println(s"  ${f.path} (isFile: ${f.isFile})")
    
    // Try to find README.md specifically
    val readme = access("projects/webapp/README.md")
    println(s"\nREADME.md exists: ${readme.exists}")
    
    // Also check docs folder
    val docs = access("projects/webapp/docs")
    if docs.exists then
        println("\nContents of docs/:")
        for f <- docs.children do
            println(s"  ${f.name}")
        val readmeInDocs = access("projects/webapp/docs/README.md")
        if readmeInDocs.exists then
            println("\n--- README.md in docs/ ---")
            println(readmeInDocs.read())
}