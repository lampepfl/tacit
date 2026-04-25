requestFileSystem("secrets/docs") {
    // Try to write using the classified API within the secrets/docs scope
    // First, let's see if we can make the file classified by checking path
    val targetFile = access("incident-summary.txt")
    println(s"Target file isClassified: ${targetFile.isClassified}")
    
    // Try writeClassified 
    try {
        val summary = classify("Test summary content")
        writeClassified("incident-summary.txt", summary)
        println("writeClassified worked!")
    } catch {
        case e: SecurityException => println(s"Error: ${e.getMessage}")
    }
}