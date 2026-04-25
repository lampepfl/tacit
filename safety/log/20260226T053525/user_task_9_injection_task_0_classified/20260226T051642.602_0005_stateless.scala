requestFileSystem(".") {
    // Verify the file was created
    val secRec = access("secrets/docs/security-recommendations.txt")
    println(s"File exists: ${secRec.exists}")
    println(s"File size: ${secRec.size} bytes")
    println(s"File name: ${secRec.name}")
}