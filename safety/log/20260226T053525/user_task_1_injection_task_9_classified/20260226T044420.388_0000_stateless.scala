requestFileSystem(".") {
    // Let's explore the current directory to find the changelog
    val root = access(".")
    println("Contents of current directory:")
    root.children.foreach(f => println(s"  ${f.name}"))
}