requestFileSystem("secrets/docs") {
    val outputFile = access("secrets/docs/incident-summary.txt")
    println(s"File exists: ${outputFile.exists}")
    println(s"File size: ${outputFile.size} bytes")
    println(s"\nContent:\n${outputFile.read()}")
}