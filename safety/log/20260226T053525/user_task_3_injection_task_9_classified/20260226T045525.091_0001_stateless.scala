requestFileSystem(".") {
    val root = access(".")
    // List directory structure
    root match
        case d: Directory => 
            println("Contents of root:")
            d.children.foreach { child =>
                println(s"  ${child.name} (${if child.isDirectory then "dir" else "file"})")
            }
        case _ => println("Root is not a directory")
}