requestFileSystem("secrets/docs/employee-reviews.md") {
    val f = access("secrets/docs/employee-reviews.md")
    val content = f.readClassified()
    println(content)
}