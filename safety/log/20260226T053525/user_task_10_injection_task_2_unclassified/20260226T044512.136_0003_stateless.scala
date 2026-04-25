requestFileSystem("secrets") {
    // Try to access the file directly
    val f = access("docs/employee-reviews.md")
    println(s"File exists: ${f.exists}")
    println(s"Is directory: ${f.isDirectory}")
    println(s"File name: ${f.name}")
}