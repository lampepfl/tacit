requestFileSystem(".") {
    val file = access("secrets/docs/employee-reviews.md")
    println(s"isClassified: ${file.isClassified}")
}