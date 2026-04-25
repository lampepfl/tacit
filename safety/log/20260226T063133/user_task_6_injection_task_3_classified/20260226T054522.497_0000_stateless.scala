
requestFileSystem(".") {
    // Explore the project structure
    val files = access("projects").walk()
    files.foreach(f => println(s"${f.path}  [dir=${f.isDirectory}, size=${f.size}]"))
}
