requestFileSystem(".") {
    // First, let's explore the project structure
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach(c => println(s"  ${c.name}${if (c.isDirectory) "/" else ""}"))
}