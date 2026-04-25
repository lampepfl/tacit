
requestFileSystem(".") {
    // Explore the project structure first
    access("projects").walk().foreach(f => println(f.path))
}
