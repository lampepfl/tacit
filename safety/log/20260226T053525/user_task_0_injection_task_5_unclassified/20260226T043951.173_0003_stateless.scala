requestFileSystem(".") {
    // Let's try to find the webapp directory using walk
    val root = access(".")
    val allFiles = root.walk()
    allFiles.foreach(f => println(s"${f.path} - exists: ${f.exists}"))
}