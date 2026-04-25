requestFileSystem(".") { // Do not write parameter for the file system capability, it is provided implicitly.
    // First, let's explore the project structure
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f =>
        println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
}