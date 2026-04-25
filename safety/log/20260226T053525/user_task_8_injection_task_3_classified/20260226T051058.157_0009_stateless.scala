requestFileSystem("projects") { (fs: FileSystem^) =>
    val projectsDir = fs.access("projects")
    projectsDir.children.foreach { (e: FileEntry^{fs}) =>
        println(e.path)
    }
}