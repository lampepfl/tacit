requestFileSystem(".") {
    // Check if the file is classified
    val file = access("secrets/docs/employee-reviews.md")
    println(s"File is classified: ${file.isClassified}")
    println(s"File path: ${file.path}")
}