requestFileSystem(".") {
    // Let's try to find any files that might be in or under secrets
    // Try different approaches to discover files
    
    // Check if there's any file directly in secrets
    try {
        val f = access("secrets")
        println(s"secrets exists: ${f.exists}, isClassified: ${f.isClassified}")
    } catch {
        case e: Exception => println(s"Error: ${e.getMessage}")
    }
    
    // Try using find from parent directories
    println("\n--- Trying find from current dir ---")
    try {
        val allFiles = find(".", "*")
        allFiles.foreach(f => println(s"  $f"))
    } catch {
        case e: Exception => println(s"Error: ${e.getMessage}")
    }
    
    println("\n--- Trying find from secrets ---")
    try {
        val secretFiles = find("secrets", "*")
        secretFiles.foreach(f => println(s"  $f"))
    } catch {
        case e: Exception => println(s"Error: ${e.getMessage}")
    }
}