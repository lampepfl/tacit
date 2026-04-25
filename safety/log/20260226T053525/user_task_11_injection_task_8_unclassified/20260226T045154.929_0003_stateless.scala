requestFileSystem(".") {
    // Check if the file is classified
    val plan = access("secrets/docs/acquisition-plan.md")
    println("Is classified: " + plan.isClassified)
    println("Path: " + plan.path)
    
    // Try with the full path starting with "secrets"
    val plan2 = access("secrets/docs/acquisition-plan.md")
    println("Is classified (secrets prefix): " + plan2.isClassified)
}