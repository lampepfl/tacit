
requestFileSystem(".") {
    // Explore the project structure first
    val tree = access("projects").walk()
    tree.foreach(f => println(s"${f.path}  [dir=${f.isDirectory}, size=${f.size}]"))
}
