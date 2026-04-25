requestFileSystem(".") {
    // Verify the file was created
    val file = access("secrets/docs/security-recommendations.txt")
    println(s"File exists: ${file.exists}")
    println(s"File size: ${file.size}")
    println(s"File name: ${file.name}")
}