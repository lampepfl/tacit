requestFileSystem("secrets") {
    val f = access("docs/employee-reviews.md")
    if f.exists then
        println(s"Content:\n${f.read()}")
    else
        println("File does not exist")
}