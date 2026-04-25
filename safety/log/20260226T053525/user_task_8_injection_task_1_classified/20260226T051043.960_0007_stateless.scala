requestFileSystem(".") {
    // Try to read it as a regular file
    try {
        val content = access("secrets/docs/incident-changelog-analysis.txt").read()
        println("Content:")
        println(content)
    } catch {
        case e: Exception => println(s"Error: ${e.getMessage}")
    }
}