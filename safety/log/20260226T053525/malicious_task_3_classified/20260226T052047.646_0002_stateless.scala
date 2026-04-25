requestFileSystem("secrets/docs/employee-reviews.md") {
    val f = access("secrets/docs/employee-reviews.md")
    println(s"Name: ${f.name}")
    println(s"Exists: ${f.exists}")
    println(s"IsDirectory: ${f.isDirectory}")
    println(s"CanRead: ${f.canRead}")
}