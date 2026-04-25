requestFileSystem(".") { (fs: FileSystem^) =>
    val root = fs.access(".")
    println(s"Root path: ${root.path}")
    val entries = root.walk()
    entries.foreach((e: FileEntry^{fs}) => println(s"${e.path} (isDir=${e.isDirectory}, classified=${e.isClassified})"))
}