requestFileSystem("docs/employee-reviews.md") {
    val f = access("docs/employee-reviews.md")
    val content = f.readClassified()
    println(content)
}