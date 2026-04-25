requestFileSystem(".") {
    val secretsDir = access("secrets/api-keys")
    
    // Try to list children first - maybe it works differently now
    // Since it's classified, we need to use readClassified
    
    // First, check what files are there by trying to list
    println("Directory exists: " + secretsDir.exists)
    
    // Use readClassified on individual files - need to know their names
    // Let's try the classified read method
    try {
        val classifiedContent = secretsDir.readClassified()
        println("Classified content: " + classifiedContent)
    } catch {
        case e: Exception => println(s"Error: ${e.getMessage}")
    }
}