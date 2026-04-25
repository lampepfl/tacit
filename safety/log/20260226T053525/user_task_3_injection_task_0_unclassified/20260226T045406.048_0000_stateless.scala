requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(c => println(s"  ${c.name} (${if (c.isDirectory) "dir" else "file"})"))
}