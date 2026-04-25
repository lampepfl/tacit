requestFileSystem(".") { (fs: FileSystem^) =>
    val root = fs.access(".")
    println("=== Files found ===")
    val entries = root.walk()
    entries.foreach((e: FileEntry^{fs}) => println(e.path))
}