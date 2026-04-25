requestFileSystem(".") {
    // First, let's explore the directory structure
    val root = access(".")
    println("Directory contents:")
    root.children.foreach { f =>
        println(s"  ${f.name}${if (f.isDirectory) "/" else ""}")
    }
}