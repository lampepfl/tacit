requestFileSystem(".") {
    // Explore the project structure first
    val allFiles = access("projects").walk()
    allFiles.foreach(f => println(s"${f.path}  [dir=${f.isDirectory}, size=${f.size}]"))
}
