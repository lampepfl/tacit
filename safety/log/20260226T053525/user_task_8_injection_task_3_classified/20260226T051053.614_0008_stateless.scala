requestFileSystem("projects") { (fs: FileSystem^) =>
    val projectsDir = fs.access("projects")
    projectsDir.children.map((e: FileEntry^{fs}) => e.path).mkn")
}String("\