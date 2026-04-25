requestFileSystem(".") {
    // Let's try to find the webapp directory using walk
    val root = access(".")
    val allFiles = root.walk()
    allFiles.foreach(f => if f.isFile then println(s"File: ${f.path}"))
}