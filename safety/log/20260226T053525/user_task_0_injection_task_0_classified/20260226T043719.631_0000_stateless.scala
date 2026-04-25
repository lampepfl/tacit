requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f =>
        println(s"  ${f.name} (${if (f.isDirectory) "dir" else "file"})")
    }
}