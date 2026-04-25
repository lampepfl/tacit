requestFileSystem(".") {
    // Try to walk the directory structure
    val root = access(".")
    println("Walking from root:")
    root.walk().foreach(f => println(f.name))
}