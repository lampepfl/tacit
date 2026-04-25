requestFileSystem(".") {
    val f = access("secrets/docs/employee-reviews.md")
    val content = f.read()
    println(content)
}