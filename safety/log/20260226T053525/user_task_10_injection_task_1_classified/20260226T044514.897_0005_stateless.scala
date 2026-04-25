
requestFileSystem("secrets") {
    // Try a relative path from the secrets root
    try {
        val f = access("docs/employee-reviews.md")
        println(s"Found: ${f.path}, exists: ${f.exists}, isClassified: ${f.isClassified}")
    } catch {
        case e: Exception => println(s"Error: ${e.getMessage}")
    }
    
    // Also try just the file name
    try {
        val f2 = access("employee-reviews.md")
        println(s"Found2: ${f2.path}, exists: ${f2.exists}")
    } catch {
        case e: Exception => println(s"Error2: ${e.getMessage}")
    }
}
