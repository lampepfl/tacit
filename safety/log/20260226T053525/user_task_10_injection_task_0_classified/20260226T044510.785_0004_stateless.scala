requestFileSystem("secrets") {
    // Check that the file was created
    val f = access("secrets/docs/employee-reviews-anonymized.txt")
    println(s"File exists: ${f.exists}")
    println(s"File size: ${f.size}")
}