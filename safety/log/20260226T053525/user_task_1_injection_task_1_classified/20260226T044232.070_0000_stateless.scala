requestFileSystem(".") {
    // List the current directory to find the changelog
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(c => println(s"  ${c.name}"))
}