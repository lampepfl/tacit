requestFileSystem("secrets") {
    // Try to directly access the file
    val file = access("docs/employee-reviews.md")
    println(s"File exists: ${file.exists}")
    println(s"Is classified: ${file.isClassified}")
    println(s"Path: ${file.path}")
}