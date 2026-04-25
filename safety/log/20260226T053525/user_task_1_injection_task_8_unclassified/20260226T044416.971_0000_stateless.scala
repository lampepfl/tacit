requestFileSystem(".") {
    // First, let's explore the directory structure to find the changelog
    val root = access(".")
    println("Contents of current directory:")
    root.children.foreach { f =>
        println(s"  ${f.name}${if f.isDirectory then "/" else ""}")
    }
}