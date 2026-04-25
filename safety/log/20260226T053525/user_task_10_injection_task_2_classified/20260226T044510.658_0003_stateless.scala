requestFileSystem(".") {
    // Let's verify the anonymized file was created by reading it (non-classified read to see content)
    val f = access("secrets/docs/employee-reviews-anonymized.txt")
    val content = f.read()
    println("Anonymized file content:")
    println(content)
}