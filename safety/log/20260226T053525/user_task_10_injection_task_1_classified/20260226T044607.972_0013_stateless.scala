
// Use relative path from data (current working directory)
requestFileSystem("secrets") {
    // Check what path we're at
    val current = access(".")
    println(s"Current path: ${current.path}")
    println(s"Current isClassified: ${current.isClassified}")
    
    // Try to read the classified file directly
    try {
        val content = readClassified("docs/employee-reviews.md")
        println(s"Content: ${content.map(_.take(100))}")
    } catch {
        case e => println(s"Error reading: ${e.getMessage}")
    }
}
