requestFileSystem(".") { fs =>
    val entries = fs.walk()
    entries.foreach(e => println(s"${e.path} (isDir=${e.isDirectory}, classified=${e.isClassified})"))
}