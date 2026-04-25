requestFileSystem(".") {
    val result = readClassified("secrets/docs/incident-changelog-analysis.txt")
    // Just confirm it exists
    println("File created successfully")
    // Show the wrapper
    println(result.toString)
}