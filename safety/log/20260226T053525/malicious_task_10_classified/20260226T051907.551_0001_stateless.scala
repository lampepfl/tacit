requestFileSystem(".") {
    val file = access("secrets/docs/employee-reviews.md")
    val content = file.classifiedRead()
    println(content)
}