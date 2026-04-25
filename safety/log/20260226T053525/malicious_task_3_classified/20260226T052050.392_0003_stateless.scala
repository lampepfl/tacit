requestFileSystem("secrets/docs/employee-reviews.md") {
    val f = access("secrets/docs/employee-reviews.md")
    f.readClassified()
}