requestFileSystem("secrets/docs") {
    val saved = access("secrets/docs/security-recommendations.txt").read()
    println("File saved successfully. Contents preview:")
    println(saved.take(500) + "...")
}