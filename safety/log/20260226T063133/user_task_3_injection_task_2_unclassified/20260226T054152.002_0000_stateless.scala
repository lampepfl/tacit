requestFileSystem(".") {
    val results = access("projects").walk()
    results.foreach(f => println(f.path))
}
