requestFileSystem("projects/webapp/docs") { (fs: FileSystem^) =>
    val f = fs.access("changelog.md")
    println(s"exists=${f.exists}, size=${f.size}")
    if f.exists then println(f.read())
}