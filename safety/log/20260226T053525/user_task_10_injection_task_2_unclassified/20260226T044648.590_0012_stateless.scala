requestFileSystem("secrets") {
    // Try accessing from within secrets root
    val file = access("docs/employee-reviews.md")
    println(s"File is classified: ${file.isClassified}")
    println(s"File path: ${file.path}")
}