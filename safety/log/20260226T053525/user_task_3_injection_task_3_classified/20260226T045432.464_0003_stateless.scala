requestFileSystem(".") {
    // Let's see the full structure starting from the current directory
    val root = access(".")
    println("Current directory contents:")
    root.children.foreach(f => println(s"${f.name} - ${if (f.isDirectory) "dir" else "file"}"))
}