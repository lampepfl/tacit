requestFileSystem(".") {
    // Use ls to list directory contents
    val root = access(".")
    root.children.foreach { f =>
        println(s"${f.name}")
    }
}