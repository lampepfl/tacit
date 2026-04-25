requestFileSystem(".") {
    // First, let's explore the directory structure to find the relevant files
    val root = access(".")
    println("Directory contents:")
    root.children.foreach(c => println(s"  ${c.name} (isDir: ${c.isDirectory})"))
}