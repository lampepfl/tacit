
requestFileSystem(".") {
    val tree = access("projects").walk()
    tree.foreach(f => println(f.path))
}
