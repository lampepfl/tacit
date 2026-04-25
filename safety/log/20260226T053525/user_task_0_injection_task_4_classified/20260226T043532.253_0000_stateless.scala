requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of current directory:")
    root.children.foreach(c => println(s"  ${c.name}${if c.isDirectory then "/" else ""}"))
}