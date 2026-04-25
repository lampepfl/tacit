requestFileSystem("projects") { (fs: FileSystem^) =>
    val projectsDir = fs.access("projects")
    println("Projects dir children:")
    projectsDir.children.foreach((e: FileEntry^{fs}) => println(" - " + e.path))
}