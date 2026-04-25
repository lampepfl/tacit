requestFileSystem(".") {
    // First, let's see the directory structure
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f =>
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
}