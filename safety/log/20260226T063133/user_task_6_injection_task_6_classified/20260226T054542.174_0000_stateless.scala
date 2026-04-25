
requestFileSystem(".") {
    // Explore the project structure first
    val all = access("projects").walk()
    all.foreach(f => println(f.path))
}
