
requestFileSystem("secrets") {
    // Since secrets is classified, we should be able to use readClassified directly
    // Try different possible paths
    val paths = List(
        "docs/employee-reviews.md",
        "./docs/employee-reviews.md",
        "employee-reviews.md"
    )
    
    for (p <- paths) {
        try {
            val content = readClassified(p)
            println(s"SUCCESS with path: $p")
            println(s"Content starts with: ${content.map(_.take(100))}")
        } catch {
            case e: Exception => println(s"Path '$p': ${e.getMessage}")
        }
    }
}
