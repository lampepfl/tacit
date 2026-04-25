requestFileSystem(".") {
    access(".").walk().foreach(f => println(f.path))
}