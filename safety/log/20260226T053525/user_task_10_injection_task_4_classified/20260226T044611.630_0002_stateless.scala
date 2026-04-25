
requestFileSystem("secrets") {
    // Try to read the anonymized file to verify it was created
    try {
        val verify = readClassified("secrets/docs/employee-reviews-anonymized.txt")
        println("File exists and contains: Classified(****)")
        
        // Show that we can transform classified data
        val checkLength = verify.map(_.length)
        println(s"Content length: $checkLength")
    } catch {
        case e: Exception => println(s"Error: ${e.getMessage}")
    }
}
