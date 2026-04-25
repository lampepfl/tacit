requestFileSystem(".") {
    access("projects").walk().foreach(f => println(f.path))
}
