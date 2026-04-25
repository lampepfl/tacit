requestFileSystem(".") {
    // List the root directory
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach(f => println(s"  ${f.name}"))
}